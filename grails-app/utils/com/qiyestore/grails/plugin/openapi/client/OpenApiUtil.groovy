package com.qiyestore.grails.plugin.openapi.client

import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpStatus

import grails.converters.JSON

import groovy.util.logging.Log4j
@Log4j
class OpenApiUtil {
    private static String APPKEY = ''
    private static String OPENAPI_URL = 'http://open.qiyestore.com'
    private static Map<String,String> tokenMap = new HashMap<String,String>()
    private static Map<String,String> mobileRecords = new HashMap<String,String>()

    public static void smsVerify(def uid, def mobileNumber) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/util/smsVerify") {
            header 'QY-AccessToken', tokenMap[uid]  // 客户端请求Token(可选)
            json {
                mobile = mobileNumber
                returnCode = 1
            }
        }

        log.debug "OpenAPI POST: /api/util/smsVerify [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            mobileRecords[mobileNumber] =  resp.json.verifyCode
        } else
            throw new Exception(resp.text)
    }

    public static void smsVerifyV2(def uid, def mobileNumber, def templateName, def dataArray=null) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/util/smsVerify/v2") {
            header 'QY-AccessToken', tokenMap[uid]  // 客户端请求Token(可选)
            json {
                mobile = mobileNumber
                template = templateName
                if(dataArray) datas = dataArray
                returnCode = 1
            }
        }

        log.debug "OpenAPI POST: /api/util/smsVerify [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            mobileRecords[mobileNumber] =  resp.json.verifyCode
        } else
            throw new Exception(resp.text)
    }

    public static boolean smsVerifyCheck(def uid, def mobileNumber, def code) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.put("$OPENAPI_URL/api/util/smsVerify/$mobileNumber") {
            header 'QY-AccessToken', tokenMap[uid]  // 客户端请求Token(可选)
            json {
                verifyCode = code
            }
        }

        log.debug "OpenAPI PUT: /api/util/smsVerify [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            mobileRecords.remove(mobileNumber)
            return true
        } else
            throw new Exception(resp.text)

        return false
    }

    public static void smsNotify(def uid, def mobileNumber, def templateName, def dataArray=null) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/util/smsNotify") {
            header 'QY-AccessToken', tokenMap[uid]  // 客户端请求Token(可选)
            json {
                mobile = mobileNumber
                template = templateName
                if(dataArray) datas = dataArray
            }
        }

        log.debug "OpenAPI POST: /api/util/smsNotify [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            mobileRecords[mobileNumber] =  resp.json.verifyCode
        } else
            throw new Exception(resp.text)
    }

    public static def getAppInfo(def _appId, def _system, def _versionCode=null) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.get("$OPENAPI_URL/api/appInfo?appId=$_appId&system=$_system&versionCode=${_versionCode?:''}")

        log.debug "OpenAPI GET: /api/appInfo [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return resp.json
        } else
            throw new Exception(resp.text)

        return null
    }

    public static def checkAppVersion(def _appId, def _system, def _versionCode) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.get("$OPENAPI_URL/api/appInfo/check?appId=$_appId&system=$_system&versionCode=$_versionCode")

        log.debug "OpenAPI GET: /api/appInfo/check [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return resp.json
        } else
            throw new Exception(resp.text)

        return null
    }

    public static def getAppVersionHistory(def _appId, def _system, def offset=0, def max=5) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.get("$OPENAPI_URL/api/appInfo/history?appId=$_appId&system=$_system&offset=$offset&max=$max")

        log.debug "OpenAPI GET: /api/appInfo/history [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            // println resp.body
            return resp.json
        } else
            throw new Exception(resp.text)

        return null
    }

    public static boolean saveAppVersion(def uid, def _appId, def _system, def _versionName, def _versionCode, def _downloadUrl, def _updateNote, def _updateNoteType='TEXT', def _forceUpdate=0, def _hotUpdate=0, def _updateMinVersionCode=0, def _versionLevel='A', def _status=0 ) {
        def token = tokenMap[uid]
        if(!token) throw new Exception('没有可用Token,使用open(uid)方法')

        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/appInfo") {
            header 'QY-AccessToken', token  // 客户端请求Token(可选)
            json {
                appId = _appId
                system = _system
                versionName = _versionName
                versionCode = _versionCode
                downloadUrl = _downloadUrl
                updateNote = _updateNote
                updateNoteType = _updateNoteType
                forceUpdate = _forceUpdate
                hotUpdate = _hotUpdate
                updateMinVersionCode = _updateMinVersionCode
                versionLevel = _versionLevel
                status = _status
            }
        }

        log.debug "OpenAPI POST: /api/appInfo [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return true
        } else
            throw new Exception(resp.text)

        return false
    }

    public static boolean updateAppVersion(def uid, def _appId, def _system, def _versionCode, def updateDatas) {

        def token = tokenMap[uid]
        if(!token) throw new Exception('没有可用Token,使用open(uid)方法')

        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/appInfo/update") {
            header 'QY-AccessToken', token  // 客户端请求Token(可选)
            json {
                appId = _appId
                system = _system
                versionCode = _versionCode
                updateDatas.each { k,v -> 
                    switch(k) {
                        case 'versionName': versionName = v; break;
                        case 'downloadUrl': downloadUrl = v; break;
                        case 'updateNote': updateNote = v; break;
                        case 'updateNoteType': updateNoteType = v; break;
                        case 'forceUpdate': forceUpdate = v; break;
                        case 'hotUpdate': hotUpdate = v; break;
                        case 'updateMinVersionCode': updateMinVersionCode = v; break;
                        case 'versionLevel': versionLevel = v; break;
                        case 'status': status = v; break;
                    }
                }
            }
        }

        log.debug "OpenAPI POST: /api/appInfo/update [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return true
        } else
            throw new Exception(resp.text)

        return false
    }

    public static def updateStatus(def uid, def _appId, def _system, def _versionCode, def _status=1 ) {
        def token = tokenMap[uid]
        if(!token) throw new Exception('没有可用Token,使用open(uid)方法')

        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/appInfo/status") {
            header 'QY-AccessToken', token  // 客户端请求Token(可选)
            json {
                appId = _appId
                system = _system
                versionCode = _versionCode
                status = _status
            }
        }

        log.debug "OpenAPI POST: /api/appInfo/status [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return true
        } else
            throw new Exception(resp.text)

        return false
    }


    public static String uploadFile(def uid, File _myFile) {
        def token = tokenMap[uid]
        if(!token) throw new Exception('没有可用Token,使用open(uid)方法')

        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/util/file/upload") {
            header 'QY-AccessToken', token  // 客户端请求Token(可选)
            contentType "multipart/form-data"
            myFile = _myFile
        }

        log.debug "OpenAPI POST: /api/util/file/upload [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return resp.json
        } else
            throw new Exception(resp.text)

        return false
    }

    public static String getShortUrl(String _longUrl, String _identCode = null, String _system = null) {
        
        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/util/shortUrl") {
            json {
                longUrl = _longUrl
                if(_identCode) identCode = _identCode
                if(_system) system = _system
            }
        }

        log.debug "OpenAPI POST: /api/util/shortUrl [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return resp.json.shortUrl
        } else
            throw new Exception(resp.text)

        return null
    }

    public static String getLongUrl(String _shortUrl) {
        
        RestBuilder rest = new RestBuilder()
        def resp = rest.get("$OPENAPI_URL/api/util/shortUrl/${_shortUrl}") {}

        log.debug "OpenAPI GET: /api/util/shortUrl/${_shortUrl} [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return resp.json.longUrl
        } else
            throw new Exception(resp.text)

        return null
    }


    private static String getAccessToken(def userId) {
        RestBuilder rest = new RestBuilder()
        def resp = rest.post("$OPENAPI_URL/api/auth/accessToken") {
            header 'QY-AppKey', APPKEY // 系统APPKEY(可选)
            json {
                uid = userId
                returnCode = 1
            }
        }

        log.debug "OpenAPI POST: /api/auth/accessToken [$resp.statusCode] Response: $resp.text"
        if(resp.statusCode == HttpStatus.OK) {
            return resp.json.token
        } else
            throw new Exception(resp.text)

        return null
    }


    public static void config(def appkey) {
        this.APPKEY = appkey
        log.debug "OpenAPI AppKey: $appkey"
    }

    public static def open(def userId) {
        def token = this.getAccessToken(userId)
        log.debug "OpenAPI UID: $userId, TOKEN: $token"
        if(token) {
            tokenMap[userId] = token
            return this
        }
        return null
    }
}
