cordova.define("com.varsitysoftware.cordova.androidmediametadataretriever.AndroidMediaMetaDataRetriever", function (require, exports, module) {

    window.getAndroidMetadata = function (strFilePath, strFileType, onStart, onFail) { exec(onStart, onFail, "AndroidMediaMetaDataRetriever", "getAndroidMetadata", [strFilePath, strFileType]); };

});


