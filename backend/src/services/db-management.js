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
        _photos[userId] = new Set();
    }

    photos.forEach((photo) => {
        _photos[userId].photoIdSet.add(photo.photoId);
        _photos[userId].photos.push({
            photoEncoding: photo.photoEncoding,
            encodingTechnique: photo.encodingTechnique
        });
    });
};

module.exports = {
    getUnsavedPhotoIdList,
    savePhotos
}
