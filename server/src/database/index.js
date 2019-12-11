import Sequelize from 'sequelize';

import databaseConfig from '../config/database';

import User from '../app/models/User';
import Lamp from '../app/models/Lamp';
import Relation from '../app/models/Relation';
import File from '../app/models/File';
import Notification from '../app/models/Notification';

const models = [User, Lamp, Relation, File, Notification];

class Database {
  constructor() {
    this.init();
  }

  init() {
    this.connection = new Sequelize(databaseConfig);

    models
      .map(model => model.init(this.connection))
      .map(model => model.associate && model.associate(this.connection.models));
  }
}

export default new Database();
