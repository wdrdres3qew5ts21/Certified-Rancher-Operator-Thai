from docker.io/nginx
RUN chgrp 0 -R  /etc/nginx/ && \
    chgrp 0 -R /var/cache/nginx/ && \
    chgrp 0 -R /usr/share/nginx/ && \
    chmod 770 -R /usr/share/nginx/ && \
    chmod 770 -R /var/cache/nginx/ && \
    chmod 770 -R /usr/share/nginx/  

copy dist/ /usr/share/nginx/html/


ENTRYPOINT [ "nginx", "-g","daemon off;"]