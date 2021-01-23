#!/bin/bash

for host in rancher-host kube-worker-1 kube-worker-2
do
	echo "Create Virtual Machine $host"
    # multipass launch ubuntu -n $host -m 4G -d 11G -c 2   --cloud-init vm-template.yaml
    multipass exec $host -- bash -c "sudo apt-get update -y && sudo apt-get install docker.io -y && sudo systemctl start docker && sudo systemctl enable docker &&sudo usermod -aG docker \$USER"
done

# rke up


