# todoapp-front
yarn build

# Standard Practice สำหรับ Basic Container Security ไม่ใช้ Root User 
podman build -t  quay.io/linxianer12/vue-todoapp-frontend:1.1.0  .

# ไม่ใช้ Root User แต่ต้องการ Privilege ในการ Binding Well Known Port
podman build -t quay.io/linxianer12/vue-todoapp-frontend:1.0.0 -f Dockerfile-port-80  .

# ทำงานได้แค่ใน Environment Container Root User 
podman build -t  quay.io/linxianer12/vue-todoapp-frontend:no-permission -f Dockerfile-port-80-no-group  .

# nginx เปล่าๆ
podman build -t  quay.io/linxianer12/vue-todoapp-frontend:nginx -f Dockerfile-port-80-mount-time  .

podman run -v dist:/usr/share/nginx/html/ -d -p 8080:80 linxianer12/vue-todoapp-frontend:nginx 


podman run -v dist:/usr/share/nginx/html/  -p 8080:80 docker.io/nginx:latest
podman run -p 8080:80 linxianer12/vue-todoapp-frontend:nginx 
## Project setup
```
yarn install
```

### Compiles and hot-reloads for development
```
yarn serve
```

### Compiles and minifies for production
```
yarn build
```

### Lints and fixes files
```
yarn lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
