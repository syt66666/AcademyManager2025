const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const questionnaireRouter = require('./src/api/questionnaire/submit'); // 引入路由文件

const app = express();

// 配置 CORS (跨域资源共享)
app.use(cors());

// 使用 bodyParser 中间件来解析请求体
app.use(bodyParser.json());

// 注册路由
app.use('/questionnaire', questionnaireRouter);  // 所有 /questionnaire 路径的请求将交给 questionnaireRouter 处理

// 配置数据库连接等其他服务

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`后端服务器已启动，端口：${PORT}`);
});
