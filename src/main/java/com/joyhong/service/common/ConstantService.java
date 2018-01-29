package com.joyhong.service.common;

public class ConstantService {
	
	/*
	 *  Device controller error
	 */
	public static int statusCode_101 = 101;		// Unable to find the device
	public static int statusCode_102 = 102;		// Update device failed, please try again later
	public static int statusCode_103 = 103;		// Unable to find the relationship between user and device
	public static int statusCode_104 = 104;		// Update user status failed, please try again later
	public static int statusCode_105 = 105;		// The user has been deleted
	public static int statusCode_106 = 106;		// Unknow action
	public static int statusCode_107 = 107;		// Update user device relationship failed, please try again later
	public static int statusCode_108 = 108;		// The device already been bound
	public static int statusCode_109 = 109;		// Unable to find the user
	public static int statusCode_110 = 110;		// Update device name failed, please try again later
	public static int statusCode_111 = 111;		// Update device status failed, please try again later
	/*
	 *  Success
	 */
	public static int statusCode_200 = 200;
	/*
	 *  Upload controller error
	 */
	public static int statusCode_301 = 301;		// File-MD5 property is not set or format error
	public static int statusCode_302 = 302;		// File-Size property format error
	public static int statusCode_303 = 303;		// File-Size property is not set
	public static int statusCode_304 = 304;		// Start bytes is not set or format error
	public static int statusCode_305 = 305;		// End bytes is not set or format error
	public static int statusCode_306 = 306;		// File-Range property is not set or format error
	public static int statusCode_307 = 307;		// The start bytes must be more than 0
	public static int statusCode_308 = 308;		// The end bytes must be more or equal than start bytes
	public static int statusCode_309 = 309;		// The end bytes must be less or equal than file size
	public static int statusCode_310 = 310;		// Content-Disposition property format error
	public static int statusCode_311 = 311;		// Content-Disposition property is not set
	public static int statusCode_312 = 312;		// The size of the Body is not equal to the size of the File-Range
	public static int statusCode_313 = 313;		// User-Id property format error
	public static int statusCode_314 = 314;		// User-Id property is not set
	public static int statusCode_315 = 315;		// Device-Id property format error
	public static int statusCode_316 = 316;		// Device-Id property is not set
	public static int statusCode_317 = 317;		// File-Desc property is not set
	public static int statusCode_318 = 318;		// Save file url to database failed, please try again later
	public static int statusCode_319 = 319;		// The length of the file_desc is not equal to the length of the file
	public static int statusCode_320 = 320;		// Current block is not set or format error
	public static int statusCode_321 = 321;		// Total block is not set or format error
	public static int statusCode_322 = 322;		// File-Block property is not set or format error
	public static int statusCode_323 = 323;		// The current block must be more than 0
	public static int statusCode_324 = 324;		// The total block must be more than 0
	public static int statusCode_325 = 325;		// The total block must be more or equal than current block
	public static int statusCode_326 = 326;		// Block file not exists
	/*
	 * User controller error
	 */
	public static int statusCode_401 = 401;		// The parameter user_imei should be a number
	public static int statusCode_402 = 402;		// The parameter user_imei should be 15 digits
	public static int statusCode_403 = 403;		// User registration failed, please try again later
	public static int statusCode_404 = 404;		// Update user profile failed, please try again later
	public static int statusCode_405 = 405;		// Unable to find the user
	public static int statusCode_406 = 406;		// Unable to find the app name
	/*
	 * Failed with msg, error message get from other website, so you should read msg parameter to get error details.
	 */
	public static int statusCode_500 = 500;
	/*
	 *  Service error
	 */
	public static int statusCode_901 = 901;		// file not exists
	public static int statusCode_902 = 902;		// file already exists
}