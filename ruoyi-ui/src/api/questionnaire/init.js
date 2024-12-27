router.post('/questionnaire/init', async (req, res) => {
  try {
    await db.query(`
      INSERT INTO user_questionnaire_status (user_id, questionnaire_id)
      SELECT u.user_id, q.id
      FROM sys_user u, questionnaires q
      WHERE NOT EXISTS (
        SELECT 1 FROM user_questionnaire_status us
        WHERE us.user_id = u.user_id AND us.questionnaire_id = q.id
      )
    `);

    res.status(200).json({ message: '问卷初始化成功' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: '问卷初始化失败' });
  }
});
