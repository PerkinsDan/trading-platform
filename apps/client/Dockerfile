FROM node:23-alpine

WORKDIR /app

COPY package*.json ./

ENV PORT=5173

ENV VITE_APP_FIREBASE_API_KEY=$VITE_APP_FIREBASE_API_KEY
RUN echo "VITE_APP_FIREBASE_API_KEY=$VITE_APP_FIREBASE_API_KEY" > .env.VITE_APP_FIREBASE_API_KEY

RUN npm install

RUN npm i -g serve

COPY . .

RUN npm run build

EXPOSE 5173

CMD ["serve","-s","dist","-l","5173"]
