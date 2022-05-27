var express = require('express');
var router = express.Router();

router.get('/test', function(req, res) {
  res.send('We have always looked into chaos and called it God. I am God!');
});

router.get('/photos', function(req, res) {
  let phoneId = req.body.phoneId;
  let photoIdList = req.body.photoIdList;

  if (phoneId == null || photoIdList == null) {
    res
      .status(400)
      .json({ message: 'Following body required: {phoneId:string, photoIdList:[string]}' });
  } else {
    res
      .status(200)
      .json({ phoneId: phoneId, photoIdList: ['p1', 'p2', 'p3'] });
  }
});

router.put('/photos', function(req, res) {
  let phoneId = req.body.phoneId;
  let photoList = req.body.photoList;

  if (phoneId == null || photoList == null ) {
    res
      .status(400)
      .json({ message: 'Following body required: {phoneId:string, photoList:['
                        + '{photoId:string, photoEncoding:string, encodingTechnique:string}]}' });
  } else {
    let photos = [];
    let validPhotosPayload = true;
    for (let i = 0; i < photoList.length; i++) {
      let photoId = photoList[i].photoId;
      let photoEncoding = photoList[i].photoEncoding;
      let encodingTechnique = photoList[i].encodingTechnique;

      if (photoId == null || photoEncoding == null || encodingTechnique == null) {
        res
          .status(400)
          .json({ message: 'Each object from photoList must have'
                           + ' {photoId:string, photoEncoding:string, encodingTechnique:string}' });
        validPhotosPayload = false;
        break;
      } else {
        photos.push({
          photoId: photoId,
          photoEncoding: photoEncoding,
          encodingTechnique: encodingTechnique
        })
      }
    }

    if(validPhotosPayload === true) {
      res
      .status(200)
      .json(photos);
    }
  }
});

module.exports = router;
