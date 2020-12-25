# System Requirement
1. Fedora 33 Workstation สำหรับทำงาน Ram 16GB, 4 Core - 8 Thread : Yoga C930 
2. Multipass Ubuntu Virtualization ใช้ในการสร้าง VM ผ่าน Command Line
3. QEMU และ LibVirt สำหรับให้ Multipass มาสั่ง Provisioning VM ใน Fedora Workstation
4. VM ทั้งสามเครื่อง Spec คือ Ram 4GB, Disk 12 GB, CPU 2 Virtual-Core 


openssl genrsa -out rke.pem 2048
openssl rsa -in rke.pem -pubout -out rke.crt


```
Name                    State             IPv4             Image
kube-master             Running           192.168.122.27   Ubuntu 20.04 LTS
kube-worker             Running           192.168.122.242  Ubuntu 20.04 LTS
rancher-host            Running           192.168.122.216  Ubuntu 20.04 LTS
```
ssh-keygen
copy public key ไฟล์ไปวางที่ .ssh/authorized_keys 

ใช้  multipass อนุญาต firewalld ด้วย

multipass launch ubuntu -n rancher-host -m 4G -d 12G -c 2   --cloud-init vm-template.yaml

multipass launch ubuntu -n kube-master -m 4G -d 12G -c 2    --cloud-init vm-template.yaml

multipass launch ubuntu -n kube-worker -m 4G -d 12G -c 2    --cloud-init vm-template.yaml

sudo apt-get update -y && sudo apt-get install docker.io -y && sudo systemctl start docker && sudo systemctl enable docker &&sudo usermod -aG docker $USER

echo ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDIJHv5t6vgeEMVHW0JoPvJ3oV7v3mRh+1kctpU7AcReNOaAJ+D9PkcOtHo+jFfMHu2+1kAWbgfrb22d9yDVWnZfn397coyPKaCHJwQMwFg27rV8xSXf0qlRuJcgcoi4M7djpuF2JqsXJ3VHuj7C4/WKUJO5tmD4AZ2ny1BhsdTJKEBmCLFza39NmtrVUuvpg11/ycazkkQwjsXr8v/QNFbsrT/oFG9VY2N+Y81PBG5m1wPFrQw60TyxrBJEeXehgdI/zQJv50cRSO9ItczQLzYKvq5fVXthhIZ2QbANtGZT+rCDTEq+jXa9SUznXYLinmXojbJsPW7S/p6eo4GsknF6SlmDdwiXzrC8uq2ycaxEndr5Scs74uTsrCIzDbMi+Ha5WqHXkJptjf0KnaboTdRFz7xu19TUo57KHjccbjNsf2081BMz9o7g3r/bU6y6RF0ELLt/EfH8mKKTpN5JK1jM7W/J0bBI9PgbLgcw8BsWqcpf8XiF1vR1mE5Uj4ITG8= linxianer12@fedora >> ~/.ssh/authorized_keys


# Prerequisite
Set up the Rancher server’s local Kubernetes cluster.

The cluster requirements depend on the Rancher version:
1. As of Rancher v2.5, Rancher can be installed on any Kubernetes cluster. This cluster can use upstream Kubernetes, or it can use one of Rancher’s Kubernetes distributions, or it can be a managed Kubernetes cluster from a provider such as Amazon EKS. > Note: To deploy Rancher v2.5 on a hosted Kubernetes cluster such as EKS, GKE, or AKS, you should deploy a compatible Ingress controller first to configure SSL termination on Rancher..
2. In Rancher v2.4.x, Rancher needs to be installed on a K3s Kubernetes cluster or an RKE Kubernetes cluster.
3. In Rancher prior to v2.4, Rancher needs to be installed on an RKE Kubernetes cluster.



# Install Rancher
```
kubectl create namespace cert-manager
helm repo add jetstack https://charts.jetstack.io

helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --version v1.1.0 \
  --set installCRDs=true

helm repo update

kubectl create namespace cattle-system

helm install rancher rancher-latest/rancher \
  --namespace cattle-system \
  --set hostname=rancher.cloudnative

helm list
```

# Backup Snapshot
ตอน Snapshot Restore ระบบจะ Down ลงไปสักพักนึงอย่างที่เราเทสกันเพราะว่ามัน restore ETCD 
```
rke etcd snapshot-save --name [ชื่อ] vanila-system  # ไฟล์จะถูกเก็บใน /opt/rke/etcd-snapshots เวลา restore ให้ระบุชื่อไฟล์นั้นลงไป
```
Highlight อยู่ที่ Control Plane
ถ้าเป็น Hosted Provider ที่ Rancher ไม่ไได้คุม Control Plane ก็จะไม่สามารถทำเรื่องของการ Backup ได้เพราะว่าไมีสามารถ Access Control Plane แต่ทำแบบ Life Cycle Delete ให้อะไรได้แบบนี้ (เพราะมี API KEY Cloud )
ถ้าเป็น Import ก็คือไม่สามารถทได้ทั้ง Life Cycle และการ Backup แต่ทำได้แค่เรื่องของการ Deploy อย่างเดียวนั่นเอง เพราะว่าไม่มี Key Cloud และก็คุม Control Plane ไมไ่ด้ด้วย

Window สามารถทำเป็น Worker ได้แต่ถ้า Etcd/ Control Plane ต้อง Linux เท่านั้น

# Debug Control Plane
เราจะสังเกตเห็นว่า Leader นั้นวิธีการดูจะต่างจาก Kubernetes ปกติเพราะว่าเราไม่มี Kubelet ตรงๆหรือ API Server Command แต่เราสามารถดูได้จากการไปดูที่ Endpoint แล้วดูจาก Annotation ที่แปะไว้ว่าใครเป็น Leader ผ่านคำสั่ง 
แต่ความน่าสนใจคือ Kubelet ปกติที่ต้องเป็น Service จริงๆลงผ่าน systemd แต่มันจะไม่มีใน Rancher เพราะใช้ทุกอย่างเป้น Docker แทนโดยเราจะเห็น Service ได้จากการลองดูผ่าน Container ของ Docker ผ่าน docker ps ได้นั่นเอง (แทน kubelet แบบ service)
ETCD, kube-apiserver และ kube-scheduler นั้นจะดูได้ผ่าน Docker จริงๆเท่านั้นไม่เห็นบน kubectl (ในนั้นมีแค่ Core-DNS)
```
kubectl get ep -n kube-system  [ชื่อ endpoint] -oyaml

apiVersion: v1
kind: Endpoints
metadata:
  annotations:
    control-plane.alpha.kubernetes.io/leader: '{"holderIdentity":"kube-master_fd8f3184-49bc-4c67-866b-290a91168bdd","leaseDurationSeconds":15,"acquireTime":"2020-12-25T09:22:43Z","renewTime"
:"2020-12-25T09:23:47Z","leaderTransitions":2}' 
  creationTimestamp: "2020-12-25T06:49:31Z"
  name: kube-controller-manager
  namespace: kube-system
  resourceVersion: "42493"
  selfLink: /api/v1/namespaces/kube-system/endpoints/kube-controller-manager
  uid: 5976f823-682e-41ec-a368-f2fb07336361
[linxianer12@fedora rancher-operator]$ 
[linxianer12@fedora rancher-operator]$ kubectl  -n kube-system get ep kube-scheduler  -oyaml    
apiVersion: v1
kind: Endpoints
metadata:
  annotations:
    control-plane.alpha.kubernetes.io/leader: '{"holderIdentity":"kube-master_50422138-65f6-42ef-b713-ee4008300a64","leaseDurationSeconds":15,"acquireTime":"2020-12-25T09:22:59Z","renewTime"
:"2020-12-25T09:23:51Z","leaderTransitions":2}' 
  creationTimestamp: "2020-12-25T06:49:33Z"
  name: kube-scheduler
  namespace: kube-system
  resourceVersion: "42516"
  selfLink: /api/v1/namespaces/kube-system/endpoints/kube-scheduler
  uid: 16cb8db2-62a9-49b4-96b4-56882fac1173
[linxianer12@fedora rancher-operator]$ 
```

# Debug Nginx Proxy
Nginx Proxy มีไว้สำหรับทำให้ Pod ที่ไมไ่ด้รันอยู๋ใน Control Plane Node สามารถติดต่อไปหา Control Plane ที่มี API-Server ได้เพราะว่า Nginx Proxy มาทำ Upstreeam จำลองเอาไว้ ต้องไปดูใน Docker Container ของแต่ล่ะ Node นั้น ซึ่งตัว Container Nginx Proxy นั้นจะใช้ Network แบบ host ทำให้สามารถเรียกใช้ผ่าน localhost ได้ที่ port 6443 ของแต่ล่ะ Worker Node เมื่อยิงไปที่ local ตัวเองแต่การ proxy ก็จะส่งไปให้เครื่อง Control Plane ได้นั่นเอง !!!
### /etc/nginx/nginx.conf
````
stream {                                
        upstream kube_apiserver {       
                                        
            server 192.168.122.216:6443;
                                       
            server 192.168.122.27:6443;
                                               
        }                                      
                                               
        server {                         
            listen        6443;          
            proxy_pass    kube_apiserver;
            proxy_timeout 30;                  
            proxy_connect_timeout 2s;          
                                               
        }                                      
                                               
} 

ubuntu@kube-worker:~$ curl localhost:6443
Client sent an HTTP request to an HTTPS server.
ubuntu@kube-worker:~$ curl https://localhost:6443
ubuntu@kube-worker:~$ curl -k https://localhost:6443
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {
    
  },
  "status": "Failure",
  "message": "Unauthorized",
  "reason": "Unauthorized",
  "code": 401
}

ubuntu@kube-worker:~$ netstat -tlpn                                                                                                                                                          
(Not all processes could be identified, non-owned process info                                                                                                                                
 will not be shown, you would have to be root to see it all.)                                                                                                                                 
Active Internet connections (only servers)                                                                                                                                                    
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name                                                                                              
tcp        0      0 0.0.0.0:443             0.0.0.0:*               LISTEN      -                                                                                                             
tcp        0      0 0.0.0.0:443             0.0.0.0:*               LISTEN      -                                                                                                             
tcp        0      0 127.0.0.1:10245         0.0.0.0:*               LISTEN      -                                                                                                             
tcp        0      0 127.0.0.1:10246         0.0.0.0:*               LISTEN      -                                                                                                             
tcp        0      0 127.0.0.1:10247         0.0.0.0:*               LISTEN      -                                                                                                             
tcp        0      0 127.0.0.1:42919         0.0.0.0:*               LISTEN      -                                                                                                             
tcp        0      0 127.0.0.1:10248         0.0.0.0:*               LISTEN      -                                                                                                             
tcp        0      0 127.0.0.1:10249         0.0.0.0:*               LISTEN      -                    
tcp        0      0 127.0.0.1:9099          0.0.0.0:*               LISTEN      -                    
tcp        0      0 0.0.0.0:6443            0.0.0.0:*               LISTEN      -                    
tcp        0      0 0.0.0.0:80              0.0.0.0:*               LISTEN      -                    
tcp        0      0 0.0.0.0:80              0.0.0.0:*               LISTEN      -           

```
# Debug Control Plane Component แบบไที่ไม่ใช้ Systemd เป้น Service แต่ใช Container แทน
```
ubuntu@kube-worker:~$ docker network ls                                                                                                                                                       
NETWORK ID          NAME                DRIVER              SCOPE                                                                                                                             
9dcab653458d        bridge              bridge              local                                                                                                                             
23bf96ab0ef4        host                host                local                                                                                                                             
e3ce3961cbc0        none                null                local                                                                                                                             
ubuntu@kube-worker:~$ docker network inspect 23b     

[    
    {                                                                                                                                                                                                                                                                                                                                                                                                     "Name": "host",
        "Id": "23bf96ab0ef46665091168dae64213d4ad737f9cfcc8bcdb5dabd58351b0be3f",                                                                              
        "Created": "2020-12-25T13:43:44.977522284+07:00",                                                                                                      
        "Scope": "local",                                                                                                                                      
        "Driver": "host",                                                                                                                                      
        "EnableIPv6": false,                                                                                                                                   
        "IPAM": {                                                                                                                                              
            "Driver": "default",                                                                                                                               
            "Options": null,                                                                                                                                   
            "Config": []                                                                                                                                       
        },                                                                                                                                                     
        "Internal": false,                                                                                                                                     
        "Attachable": false,                                                                                                                                   
        "Ingress": false,                                                                                                                                      
        "ConfigFrom": {                                                                                                                                        
            "Network": ""                                                                                                                                      
        },                                                                                                                                                     
        "ConfigOnly": false,                                                                                                                                   
        "Containers": {                                                                                                                                        
            "1f64516f3260b76b7b30a14abb1d017bf2efcb4edd243f9da8c53c898d0bf7f7": {                                                                              
                "Name": "k8s_POD_nginx-ingress-controller-ffqhb_ingress-nginx_0c8934f9-b7a6-4fa7-be51-1dcd5ec254f2_0",                                                                                                                                                                                                        
                "EndpointID": "c29c3b8e53c7cba8eb135b849675c465b6f1ebbf0d20770f75f1561797f7684d",                                                                                                                                                                                                                             
                "MacAddress": "",                                                                                                                              
                "IPv4Address": "",                                                                                                                             
                "IPv6Address": ""                                                                                                                              
            },                                                                                                                                                 
            "2ba19e6d704753104065f593af17be3cc033c6d041d45ecaeef86d32b28282b3": {                                                                              
                "Name": "nginx-proxy",                                                                                                                         
                "EndpointID": "7bd2e2f6e15662334a571b8a913549a182db5daf0f3d3f35ebd9b3d6e67aeb81",                                                                                                                                                                                                                             
                "MacAddress": "",                                                                                                                              
                "IPv4Address": "",                                                                                                                             
                "IPv6Address": ""                                                                                                                              
            },                                                                                                                                                 
            "365a8024834d8e00197c13c86a509a365594bd16eb3b71d1efcce092a2fb0ce9": {                                                                              
                "Name": "kubelet",                                                                                                                             
                "EndpointID": "17165544d3c30458d41b05520dd1f41f4a26a0a12af23361d274cc58addf7647",                                                                                                                                                                                                                             
                "MacAddress": "",                                                                                                                              
                "IPv4Address": "",                                                                                                                             
                "IPv6Address": ""                                                                                                                              
            },                                                                                                                                                 
            "78b96a4e35593e01d84a260f46d205f5676b43a8a0e1bd6dcd0c345f648ead6f": {                                                                              
                "Name": "k8s_POD_calico-node-7pg7v_kube-system_224d2e6b-6504-4dd3-925d-635cfa5ddbfc_0",                                                                                                                                                                                                                       
                "EndpointID": "9f0d534a83f89b1557b4eeff9ace367f9774a4baa9cbe83a4c789aebcd3dfefc",                                                                                                                                                                                                                             
                "MacAddress": "",                                                                                                                              
                "IPv4Address": "",                                                                                                                             
                "IPv6Address": ""                                                                                                                              
            },                                                                                                                                                 
            "d248e851654718c6fcb78f0ace580bc3003ccb25511563edc180c64b00def526": {                                                                              
                "Name": "kube-proxy",                                                                                                                          
                "EndpointID": "543c11f36ae1c16f94207170906c38f5783501f5361477e70d02aae31c6692c8",                                                                                                                                                                                                                             
                "MacAddress": "",                                                                                                                              
                "IPv4Address": "",                                                                                                                             
                "IPv6Address": ""                                                                                                                              
            }                                                                                                                                                  
        },                                                                                                                                                     
        "Options": {},                                                                                                                                         
        "Labels": {}                                                                                                                                           
    }                                                                                                                                                          
]   

```# Rancher-Certified-Operator
