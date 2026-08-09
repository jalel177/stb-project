#!/bin/bash
dnf install -y iptables
sysctl -w net.ipv4.ip_forward=1
echo "net.ipv4.ip_forward = 1" >> /etc/sysctl.conf

EXT_IF=$(ip -o -4 route show to default | awk '{print $5}')
iptables -t nat -A POSTROUTING -o "$EXT_IF" -j MASQUERADE
iptables -F FORWARD
iptables -A FORWARD -j ACCEPT

mkdir -p /etc/iptables
iptables-save > /etc/iptables/rules.v4

cat <<'EOF' >/etc/systemd/system/restore-iptables.service
[Unit]
Description=Restore iptables NAT rules on boot
After=network.target

[Service]
Type=oneshot
ExecStart=/sbin/iptables-restore /etc/iptables/rules.v4
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF

systemctl enable restore-iptables.service
