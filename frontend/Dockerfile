FROM node:17-alpine3.12
ENV APP_HOME=/app/frontend
WORKDIR $APP_HOME

COPY package*.json $APP_HOME
COPY public/ $APP_HOME/public
COPY src/ $APP_HOME/src

RUN npm install

EXPOSE 3000

CMD ["npm", "start"]