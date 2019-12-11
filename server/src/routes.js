import { Router } from 'express';
import multer from 'multer';

import multerConfig from './config/multer';
import UserController from './app/controllers/UserController';
import LampController from './app/controllers/LampController';
import SessionController from './app/controllers/SessionController';
import RelationCotroller from './app/controllers/RelationController';
import FileController from './app/controllers/FileController';
import NotificationController from './app/controllers/NotificationController';

import authMiddleware from './app/middlewares/auth';

const routes = new Router();
const upload = multer(multerConfig);

routes.post('/users', UserController.store);
routes.post('/sessions', SessionController.store);

routes.use(authMiddleware);

routes.get('/users', UserController.index);

routes.get('/lamps', LampController.index);
routes.post('/lamps', LampController.store);
routes.put('/lamps/:code', LampController.update);

routes.get('/relations', RelationCotroller.index);
routes.post('/relations/:login', RelationCotroller.store);
routes.put('/relations', RelationCotroller.update);
routes.delete('/relations', RelationCotroller.delete);

routes.get('/notifications', NotificationController.index);

routes.post('/files', upload.single('file'), FileController.store);

export default routes;
