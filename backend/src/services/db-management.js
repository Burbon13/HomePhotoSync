const fs = require('fs');

const PATH_STORAGE = './tmp/';
const PATH_DB = PATH_STORAGE + 'photos.json';

// A painful, stupid way of checking if DB exists
if (!fs.existsSync(PATH_DB)) {
  fs.writeFileSync(PATH_DB, JSON.stringify({}));
}

let _photos = JSON.parse(fs.readFileSync(PATH_DB));

let getUnsavedPhotoIdList = (userId, photoIdList) => {
  if (!(userId in _photos)) {
    _photos[userId] = {
      photoIdSet: []
    };
  }

  let unsavedPhotos = [];
  for (let i = 0; i < photoIdList.length; i++) {
    let photoId = photoIdList[i];
    if (!_photos[userId].photoIdSet.includes(photoId)) {
      unsavedPhotos.push(photoId);
    }
  }
  return unsavedPhotos;
};

let savePhotos = (userId, photos) => {
  if (!(userId in _photos)) {
    _photos[userId] = {
      photoIdSet: []
    };
  }

  photos.forEach((photo) => {
    _photos[userId].photoIdSet.push(photo.photoId);
    fs.writeFile(PATH_STORAGE + photo.photoId, photo.photoEncoding.data, 'base64', function (err) {
      console.log(err);
    });
  });
  fs.writeFileSync(PATH_DB, JSON.stringify(_photos));
};

module.exports = {
  getUnsavedPhotoIdList,
  savePhotos
}
