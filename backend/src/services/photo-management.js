const db = require('./db-management');

let retrieveUnsavedPhotos = (userId, photoIdList) => {
    return db.getUnsavedPhotoIdList(userId, photoIdList);
};

let savePhotos = (userId, photos) => {
    db.savePhotos(userId, photos);
};

module.exports = {
    retrieveUnsavedPhotos,
    savePhotos
}
