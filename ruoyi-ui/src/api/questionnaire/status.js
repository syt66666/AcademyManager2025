router.get('/questionnaire/status', async (req, res) => {
  const { userId, questionnaireId } = req.query;

  if (!userId || !questionnaireId) {
    return res.status(400).json({ message: '缺少必要参数' });
  }

  try {
    const [status] = await db.query(
      'SELECT has_answered FROM user_questionnaire_status WHERE user_id = ? AND questionnaire_id = ?',
      [userId, questionnaireId]
    );

    if (status) {
      res.status(200).json({ hasAnswered: status.has_answered });
    } else {
      res.status(200).json({ hasAnswered: false }); // 没有记录表示未回答
    }
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: '检查问卷状态失败' });
  }
});
