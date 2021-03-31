FROM node:latest  as NG_BUILDER
ARG CONFIGURATION=heroku
RUN npm install -g @angular/cli

RUN mkdir -p /tmp/build
WORKDIR /tmp/build

COPY package.json package-lock.json /tmp/build/
RUN npm install
ENV PATH /tmp/build/node_modules/.bin:$PATH

COPY ./ /tmp/build/
RUN npm i -g @angular/cli
RUN ng build --output-path=./dist/out --configuration=$CONFIGURATION

FROM nginx
ARG PORT=4200
EXPOSE $PORT
ENV PORT=$PORT
ENV CONFIGURATION=$CONFIGURATION

RUN rm -rf /etc/nginx/sites-enabled/*
RUN rm -rf /usr/share/nginx/html/*
RUN rm -rf /etc/nginx/conf.d/*

COPY --from=NG_BUILDER /tmp/build/dist/out /usr/share/nginx/html/
COPY ./nginx/templates/default.conf.template /etc/nginx/templates/

COPY ./nginx/env-print.sh /docker-entrypoint.d/
RUN chmod +x /docker-entrypoint.d/env-print.sh
