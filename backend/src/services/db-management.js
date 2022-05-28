const fs = require('fs');
const levelup = require('levelup');
const leveldown = require('leveldown');

const PATH_TO_STORAGE = './tmp/';

const mydb = levelup(leveldown('./level-db'));


// === DUMB DATABASE ===
let _photos = {};

let getUnsavedPhotoIdList = (userId, photoIdList) => {
    if (! (userId in _photos)) {
        _photos[userId] = {
            photoIdSet: new Set(),
            photos: []
        };
    }

    let unsavedPhotos = [];
    for (let i = 0; i < photoIdList.length; i ++) {
        let photoId = photoIdList[i];
        if (! _photos[userId].photoIdSet.has(photoId)) {
            unsavedPhotos.push(photoId);
        }
    }
    return unsavedPhotos;
};

let savePhotos = (userId, photos) => {
    if (! (userId in _photos)) {
        _photos[userId] = {
            photoIdSet: new Set(),
            photos: []
        };
    }

    photos.forEach((photo) => {
        _photos[userId].photoIdSet.add(photo.photoId);
        fs.writeFile(PATH_TO_STORAGE + photo.photoId, photo.photoEncoding.data, 'base64', function(err) {
            console.log(err);
        });
    });
};

module.exports = {
    getUnsavedPhotoIdList,
    savePhotos
}
