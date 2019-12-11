import Lamp from '../models/Lamp';

class LampController {
  async index(req, res) {
    const lamps = await Lamp.findAll({
      where: { user: req.userId },
    });

    if (!lamps) {
      return res
        .status(404)
        .json({ error: 'It was not possible to find lamps' });
    }

    return res.status(200).json(lamps);
  }

  async store(req, res) {
    const { id, code } = await Lamp.create();

    return res.json({ id, code });
  }

  async update(req, res) {
    const lamp = await Lamp.findOne({
      where: {
        code: req.params.code,
      },
    });

    await lamp.update({
      user: req.userId,
      nickname: req.body.nickname,
    });

    return res.status(200).json({ success: 'Lamp updated successfully' });
  }
}

export default new LampController();
