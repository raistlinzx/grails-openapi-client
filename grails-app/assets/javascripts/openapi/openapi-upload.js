//= require ../fine-uploader.min
//= require ../jquery.cookie.min
//= request_self

function OpenApiUpload(dom, callback) {
    var AWU = this;

    this.debug = false;

    this.config = {
        text: {
            uploadButton : $(dom).html()
        },
        validation : {
            // allowedExtensions : [ 'jpg', 'png', 'gif', 'jpeg', 'bmp', 'tiff' ],
            sizeLimit : 5242880
        },
        messages: {            
            // typeError: "只能上传'jpg', 'png', 'gif', 'jpeg','bmp', 'tiff'的文件",            
            sizeError: "文件大小不能超过5M"
        },
        callbacks: {
            onComplete : function(id, fileName, data) {
                if(AWU.debug) console.debug(fileName+' upload success');
                if(callback) callback(dom, data.fileUrl);
            },
            onUpload : function(id, fileName) {},
            onProgress : function(id, fileName, loaded, total) {
                if(AWU.debug) console.debug(fileName + 'uploading '+ loaded + ' of ' + total);

            },
            onError: function(id, name, reason, xhr){
                if(AWU.debug) console.debug(reason);
            }
        }
    };

    this.setConfig = function() {
        console.log('this function will come soon..');
        return this;
    };

    this.init = function(debug) {

        if(debug) this.debug = true;

        new qq.FineUploaderBasic({
            button : dom,
            request: {
                endpoint : 'http://open.qiyestore.com/api/util/file/upload',
                inputName: 'myFile',
                customHeaders: {'QY-AccessToken': $.cookie('QY-AccessToken')},
                forceMultipart: true
            },
            cors: {
                expected: true
            },
            text: this.config.text,
            validation : this.config.validation,
            messages: this.config.messages,
            callbacks: this.config.callbacks
        });

        return this;
    }
}
