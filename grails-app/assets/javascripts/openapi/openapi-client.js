//= require ../jquery.cookie.min
//= require_self

var OpenApiClient = {
	appkey: '',
	generateUUID: function() {
	    var d = new Date().getTime();
	    var uuid = 'xxxxxxxx-xxxx-xxxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
	        var r = (d + Math.random()*16)%16 | 0;
	        d = Math.floor(d/16);
	        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
	    });
	    return uuid;
	},

    getAccessToken: function(uid) {
		if(this.appkey) {
			if(uid) {
				$.cookie('QY-UID', uid, { path:'/' });
			} 

			if(!$.cookie('QY-UID')) {
				$.cookie('QY-UID', this.generateUUID(), { path:'/' });
			}

			$.ajax({
				url: 'http://open.qiyestore.com/api/auth/accessToken',
				type: 'POST',
				contentType: 'application/json',
				processData: false,
				crossDomain: true,
				headers: {'QY-AppKey': this.appkey},
				data: JSON.stringify({'uid': $.cookie('QY-UID'), 'returnCode': 1})
			}).success(function(data) {
				// console.log(data);
				$.cookie('QY-AccessToken', data.token, { path:'/' });
			});
		} else {
			console.debug('No OpenAPI AppKey');
		}
	}

}

console.debug('Load OpenApiClient OK.')