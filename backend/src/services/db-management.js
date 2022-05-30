const fs = require('fs');

const PATH_STORAGE = './tmp/';
const PATH_DB = PATH_STORAGE + 'photos.json';

// A stupidly painful way of checking if DB exists
try {
  if (fs.existsSync(path)) {
  }
} catch (err) {
  console.error(err)
}

let _photos = JSON.parse(fs.readFileSync(PATH_DB));

let getUnsavedPhotoIdList = (userId, photoIdList) => {
  if (!(userId in _photos)) {
    _photos[userId] = {
      photoIdSet: new Set(),
      photos: []
    };
  }

  let unsavedPhotos = [];
  for (let i = 0; i < photoIdList.length; i++) {
    let photoId = photoIdList[i];
    if (!_photos[userId].photoIdSet.has(photoId)) {
      unsavedPhotos.push(photoId);
    }
  }
  return unsavedPhotos;
};

let savePhotos = (userId, photos) => {
  if (!(userId in _photos)) {
    _photos[userId] = {
      photoIdSet: new Set(),
      photos: []
    };
  }

  photos.forEach((photo) => {
    _photos[userId].photoIdSet.add(photo.photoId);
    fs.writeFile(PATH_TO_STORAGE + photo.photoId, photo.photoEncoding.data, 'base64', function (err) {
      console.log(err);
    });
  });
};

module.exports = {
  getUnsavedPhotoIdList,
  savePhotos
}
