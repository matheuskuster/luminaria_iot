import { Op } from 'sequelize';
import Relation from '../models/Relation';
import User from '../models/User';
import Notification from '../models/Notification';

class RelationController {
  async store(req, res) {
    const relationAlreadyExists = await Relation.findOne({
      where: {
        [Op.or]: [
          { ask_user: req.userId },
          { accept_user: req.userId, accepted: true },
        ],
      },
    });

    if (relationAlreadyExists) {
      return res.status(401).json({ error: 'You already have a relationship' });
    }

    const logged = await User.findByPk(req.userId);

    const user = await User.findOne({
      where: { login: req.params.login },
    });

    if (!user) return res.status(401).json({ error: 'User does not exists' });

    const relation = await Relation.create({
      ask_user: req.userId,
      accept_user: user.id,
    });

    await Notification.create({
      text: `${logged.login} deseja parear com você. Deseja aceitar?`,
      to_accept: true,
      user_id: user.id,
    });

    return res.status(200).json(relation);
  }

  async index(req, res) {
    const relation = await Relation.findOne({
      where: {
        [Op.or]: [{ ask_user: req.userId }, { accept_user: req.userId }],
      },
    });

    if (!relation) {
      return res.status(404).json({ error: 'Relation was not found' });
    }

    return res.status(200).json(relation);
  }

  async update(req, res) {
    const relation = await Relation.findOne({
      where: { accept_user: req.userId, accepted: false },
    });

    await relation.update({
      accepted: true,
    });

    const notification = await Notification.findByPk(req.body.notification);

    await notification.update({
      accepted: true,
    });

    const user = await User.findOne({
      where: {
        login: req.body.login,
      },
    });

    const logged = await User.findByPk(req.userId);

    await Notification.create({
      user_id: user.id,
      text: `${logged.login} aceitou sua solicitação. Por favor, reinicie sua luminária.`,
    });

    return res.status(200).json({ success: 'Relation updated successfully' });
  }

  async delete(req, res) {
    const relation = await Relation.findOne({
      where: {
        [Op.or]: [{ ask_user: req.userId }, { accept_user: req.userId }],
      },
    });

    await relation.destroy();

    return res.status(200).json({ success: 'Relation deleted successfully' });
  }
}

export default new RelationController();
