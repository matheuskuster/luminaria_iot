import Notification from '../models/Notification';

class NotificationController {
  async index(req, res) {
    const notifications = await Notification.findAll({
      where: {
        user_id: req.userId,
      },
    });

    return res.status(200).json(notifications);
  }
}

export default new NotificationController();
