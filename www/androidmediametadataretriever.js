//cordova.define("com.varsitysoftware.cordova.androidmediametadataretriever.AndroidMediaMetaDataRetriever", function (require, exports, module) {

//    window.getAndroidMetadata = function (strFilePath, strFileType, onStart, onFail) { exec(onStart, onFail, "AndroidMediaMetaDataRetriever", "getAndroidMetadata", [strFilePath, strFileType]); };

//});

cordova.define("com.varsitysoftware.cordova.androidmediametadataretriever.AndroidMediaMetaDataRetriever", function (require, exports, module) {

    var exec = require("cordova/exec");
    var AndroidMediaMetaDataRetriever = function () { };

    AndroidMediaMetaDataRetriever.prototype.getAndroidMetadata = function (strFilePath, strFileType, onStart, onFail) {
        alert(AndroidMediaMetaDataRetriever);
        exec(onStart, onFail, "AndroidMediaMetaDataRetriever", "getAndroidMetadata", [strFilePath, strFileType]);
    };

    var androidMediaMetaDataRetriever = new AndroidMediaMetaDataRetriever();
    module.exports = androidMediaMetaDataRetriever;
});

if (!window.plugins) {
    window.plugins = {};
}

