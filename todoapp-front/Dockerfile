from docker.io/nginx
RUN chgrp 0 -R  /etc/nginx/nginx.conf && \
    chgrp 0 -R /var/cache/nginx/ && \
    chgrp 0 -R /usr/share/nginx/html/

copy dist/ /usr/share/nginx/html/


ENTRYPOINT [ "nginx", "-g","daemon off;"]