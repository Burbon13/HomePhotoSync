var express = require('express');
var router = express.Router();

router.get('/test', function(req, res, next) {
  res.send('We have always looked into chaos and called it God. I am God!');
});

module.exports = router;
