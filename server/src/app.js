import mosca from 'mosca';
import cors from 'cors';
import path from 'path';

import express from 'express';
import routes from './routes';

import './database';

class App {
  constructor() {
    this.server = express();
    this.mqtt = new mosca.Server({ port: 1883 });

    this.middlewares();
    this.routes();
  }

  middlewares() {
    this.server.use(express.json());
    this.server.use(cors());
    this.server.use(
      '/files',
      express.static(path.resolve(__dirname, '..', 'tmp', 'uploads'))
    );
  }

  routes() {
    this.server.use(routes);
  }
}

export default new App();
