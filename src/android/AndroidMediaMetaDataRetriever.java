/**
 * 
 */
/**
 * @author John Weaver for Varsity Software, Inc. 2014
 *
 */
package com.varsitysoftware.cordova.androidmediametadataretriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.provider.OpenableColumns;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.provider.Settings.Secure;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class AndroidMediaMetaDataRetriever extends CordovaPlugin {
	
    private CallbackContext callback;
        
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    	
    	//Log.i("CC", "XXXXXX = ");
    	
    	callback = callbackContext;
    	//callback.success("1");    
    	
    	try {
            
    		if ("getAndroidMetadata".equals(action)) {
    			//Log.i("CC", "AAAA = ");
    			String strMediaURL = args.getString(0);
    			String strFileType = args.getString(1);
    			
    			this.getAndroidMetadata(strMediaURL, strFileType);
    	        
    			//Log.i("CC", "ZZZZ = ");
                //callback.success("2");    
                
    	        return true;
    	    }          
            //return false;
        } catch (JSONException e) {        	
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        } catch (IOException e) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION));        	
        }
        
        return true;     
    }

    private void getAndroidMetadata(String strMediaURL, String strFileType) throws IOException {
		
		Uri uriMediaURL = Uri.parse(strMediaURL);
		Context context = this.cordova.getActivity().getApplicationContext();
		
		JSONObject returnJSON = new JSONObject();
		
		File objFile = new File(strMediaURL);
		
		int intPos = strMediaURL.indexOf("content://");
		
		if (intPos != -1)
		{
		  //Uri myUri = Uri.parse(strMediaURL);
		  String strPath = getPath(context, uriMediaURL);
		  objFile = new File(strPath);	
		}
		
		//File objFile = new File(strMediaURL);
		//int intPos = strMediaURL.lastIndexOf("/") + 1;
		//String strFileName = strMediaURL.substring(intPos);
		//File objFile = new File(Environment.getExternalStorageDirectory(), strFileName);
		
		long lngLength = objFile.length();
		long lngLastModified = objFile.lastModified() / 1000; // DIVIDE BY 1000 TO REMOVE MICROSECONDS IN 13 DIGIT NUMBER
		
		if (lngLength == 0)
		{
		  try
		  {
		    Cursor returnCursor = context.getContentResolver().query(uriMediaURL, null, null, null, null);
		    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
		    returnCursor.moveToFirst();
		    lngLength = returnCursor.getLong(sizeIndex);
		    Log.i("CC", "XXXXXXsizeIndex = " + Long.toString(returnCursor.getLong(sizeIndex)));	
		  }
		  catch (Exception e)
		  {
			Log.i("CC", "JSONException = " + e.toString());
		  }
		}
				
		String strDeviceKey = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		
		Log.i("CC", "strMediaURL = " + strMediaURL);
		Log.i("CC", "lngLength = " + lngLength);	
		Log.i("CC", "lngLastModified = " + lngLastModified);
		Log.i("CC", "strDeviceKey = " + strDeviceKey);		
		
		try
		{	
			returnJSON.put("fileCreated", lngLastModified);
			returnJSON.put("fileSize", lngLength);
			returnJSON.put("deviceKey", strDeviceKey);
			returnJSON.put("fileURL", strMediaURL);			
			returnJSON.put("fileType", strFileType);
		}
		catch (JSONException e)
		{
			Log.i("CC", "JSONException = " + e.toString());
		}		
		
		if (strFileType.equals("1")) // 1 == video
		{	
			if (strMediaURL.startsWith("content://") == true)
			{
				//String res = null;
    				//String[] proj = { MediaStore.Images.Media.DATA };
    				//Cursor cursor = context.getContentResolver().query(uriMediaURL, proj, null, null, null);
			        //if(cursor.moveToFirst()){;
			        //  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			        //  res = cursor.getString(column_index);
			        //}
			        //cursor.close();
			        //strMediaURL = res;
    
    				int intPosLast = strMediaURL.lastIndexOf("/") + 1;
				String strFileName = strMediaURL.substring(intPosLast);
				strMediaURL = Environment.getExternalStorageDirectory() + "/" + strFileName;
		
				//strMediaURL = getPath(context, uriMediaURL);
				Log.i("CC", "VIDEOstrMediaURL = " + strMediaURL);	
				//webView.sendJavascript("onAndroidFileSelectedPathError();");
				//return;
			}
			
			Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(strMediaURL,Thumbnails.MINI_KIND);
			Log.i("CC", "ERRRRRRRRR = " + strMediaURL);
			//Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(strFilePath,Thumbnails.MINI_KIND);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		    byte[] b = baos.toByteArray();	    
		    String strThumbnail = Base64.encodeToString(b,Base64.DEFAULT);
		    //Log.i("CC", "video strThumbnail = " + strThumbnail);
		    
			MediaMetadataRetriever retriever  = new MediaMetadataRetriever();
			retriever.setDataSource(strMediaURL);			
			
			String strDurationInMilliseconds = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			String strBitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
			String strFileCreated = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
			String strFileLocation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
			String strFileMimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
			String strVideoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
			String strVideoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
			String strVideoRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
			
			long lngDurationInMilliseconds = Long.parseLong( strDurationInMilliseconds );
			long lngDurationInSeconds = lngDurationInMilliseconds / 1000;
			
		    //long timeInmillisec = Long.parseLong( time );
			//long duration = timeInmillisec / 1000;
			//long hours = duration / 3600;
			//long minutes = (duration - hours * 3600) / 60;
			//long seconds = duration - (hours * 3600 + minutes * 60);
			
			String strFileOrientation = "1";
			
			if (strVideoRotation == "90")
			{
				strFileOrientation = "2";
			}
			if (strVideoRotation == "180")
			{
				strFileOrientation = "3";
			}
			if (strVideoRotation == "270")
			{
				strFileOrientation = "4";
			}
		    
		    try
			{
		    	//returnJSON.put("fileCreated", strFileCreated);
				returnJSON.put("fileWidth", strVideoWidth);
				returnJSON.put("fileHeight", strVideoHeight);
				returnJSON.put("fileOrientation", strFileOrientation);
				returnJSON.put("fileLatitude", strFileLocation.trim());
				returnJSON.put("fileLongitude", strFileLocation.trim());				
				returnJSON.put("fileFPS", "0");
				returnJSON.put("videoAngleInDegree", strVideoRotation);
				returnJSON.put("fileDuration", lngDurationInSeconds);				
				returnJSON.put("base64", strThumbnail);
				//returnJSON.put("base64", "");
				
				Log.i("CC", "fileCreated = " + strFileCreated);
				Log.i("CC", "fltImgWidth = " + strVideoWidth);
				Log.i("CC", "fltImgHeight = " + strVideoHeight);
				Log.i("CC", "fileURL = " + strMediaURL);
				Log.i("CC", "fileLatitude = " + strFileLocation);
				Log.i("CC", "fileType = " + strFileMimeType);
				Log.i("CC", "fileFPS = " + strBitrate);
				Log.i("CC", "videoAngleInDegree = " + strVideoRotation);
				Log.i("CC", "fileDuration = " + lngDurationInSeconds);
				//Log.i("CC", "fileSize = " + lngLength);
			}
			catch (JSONException e)
			{
				Log.i("CC", "JSONException = " + e.toString());
			}			
		}
		else
		{	
						
			//FileInputStream fis = new FileInputStream(strMediaURL);
            //Bitmap bmThumbnail = BitmapFactory.decodeStream(fis);
            
			//Bitmap bm = BitmapFactory.decodeFile(strMediaURL);
            
            //Bitmap bmThumbnail = ThumbnailUtils.extractThumbnail(bm, 1280, 720);
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		    //byte[] b = baos.toByteArray();	    
		    //String strThumbnail = Base64.encodeToString(b,Base64.URL_SAFE);
		    //Log.i("CC", "photo strThumbnail = " + strThumbnail);
		    
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            //bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            //byte[] b = baos.toByteArray();
            //String strThumbnail = Base64.encodeToString(b,Base64.DEFAULT);
            //Log.i("CC", "strThumbnail = " + strThumbnail);
            
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			
			if (strMediaURL.startsWith("content://") == true)
			{
				Uri uri = Uri.parse(strMediaURL);
				BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
			}
			else
			{
				BitmapFactory.decodeFile(strMediaURL, options);
			}
			
			//BitmapFactory.decodeFile(strFilePath, options);
			

			int intImageWidth = options.outWidth;
			int intImageHeight = options.outHeight;
			//String strImageMimeType = options.outMimeType;
			
			//intFileWidth = intImageWidth;
			//intFileHeight = intImageHeight;
			//strMimeType = strImageMimeType;
			
			Log.i("CC", "intImageWidth = " + intImageWidth);
			Log.i("CC", "intImageHeight = " + intImageHeight);
			
			try
			{				
				returnJSON.put("fileWidth", intImageWidth);
				returnJSON.put("fileHeight", intImageHeight);	
				//returnJSON.put("base64", strThumbnail);
				returnJSON.put("base64", "");
			}
			catch (JSONException e)
			{
			
			}
		}
		
		String strJSON = returnJSON.toString();
		//Log.i("CC", "strJSON = " + strJSON);
		
		webView.sendJavascript("onAndroidFileSelectedSuccess('" + strJSON + "');");
		
		//callback.success(returnJSON);

	}	
   
    /**
    * Get a file path from a Uri. This will get the the path for Storage Access
    * Framework Documents, as well as the _data field for the MediaStore and
    * other file-based ContentProviders.
    *
    * @param context The context.
    * @param uri The Uri to query.
    * @author paulburke
    */
    public static String getPath(final Context context, final Uri uri) 
    {
    	final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	
	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];
	
	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }
	
	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {
	
	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
	
	            return getDataColumn(context, contentUri, null, null);
	        }
	        // GoogleDriveProvider
	        else if (isGoogleDriveDocument(uri)) {
	
	            //final String id = DocumentsContract.getDocumentId(uri);
	            //final Uri contentUri = ContentUris.withAppendedId(
	            //        Uri.parse("content://com.google.android.apps.docs.storage"), id);
	
	            return uri.getPath();
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];
	
	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }
	
	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };
	
	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {
	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }
	
	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {
	
	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };
	
	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int column_index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(column_index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}
	
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Docs Provider.
	 */
	public static boolean isGoogleDriveDocument(Uri uri) {
	    return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
	}
}
