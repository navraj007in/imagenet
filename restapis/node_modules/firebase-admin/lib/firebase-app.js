/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
"use strict";
var deep_copy_1 = require('./utils/deep-copy');
/**
 * Global context object for a collection of services using a shared authentication state.
 */
var FirebaseApp = (function () {
    function FirebaseApp(options, name, firebaseInternals_) {
        var _this = this;
        this.firebaseInternals_ = firebaseInternals_;
        this.services_ = {};
        this.isDeleted_ = false;
        this.name_ = name;
        this.options_ = deep_copy_1.deepCopy(options);
        if (typeof this.options_ !== 'object' || this.options_ === null) {
            // Ensure the options are a non-null object
            this.options_ = {};
        }
        var hasCredential = ('credential' in this.options_);
        var hasServiceAccount = ('serviceAccount' in this.options_);
        var errorMessage;
        if (!hasCredential && !hasServiceAccount) {
            errorMessage = 'Options must be an object containing at least a "credential" property.';
        }
        else if (hasCredential && hasServiceAccount) {
            errorMessage = 'Options cannot specify both the "credential" and "serviceAccount" properties.';
        }
        // TODO(jwenger): NEXT MAJOR RELEASE - throw error if the "credential" property is not specified
        if (typeof errorMessage !== 'undefined') {
            throw new Error("Invalid Firebase app options passed as the first argument to initializeApp() for the " +
                ("app named \"" + this.name_ + "\". " + errorMessage));
        }
        // TODO(jwenger): NEXT MAJOR RELEASE - remove "serviceAccount" property deprecation warning
        if (hasServiceAccount) {
            /* tslint:disable:no-console */
            console.log('WARNING: The "serviceAccount" property specified in the first argument to initializeApp() ' +
                'is deprecated and will be removed in the next major version. You should instead use the ' +
                '"credential" property.');
        }
        Object.keys(firebaseInternals_.serviceFactories).forEach(function (serviceName) {
            // Defer calling createService() until the service is accessed
            _this[serviceName] = _this.getService_.bind(_this, serviceName);
        });
    }
    /**
     * Firebase services available off of a FirebaseApp instance. These are monkey-patched via
     * registerService(), but we need to include a dummy implementation to get TypeScript to
     * compile it without errors.
     */
    /* istanbul ignore next */
    FirebaseApp.prototype.auth = function () {
        throw new Error('INTERNAL ASSERT FAILED: Firebase auth() service has not been registered.');
    };
    /* istanbul ignore next */
    FirebaseApp.prototype.database = function () {
        throw new Error('INTERNAL ASSERT FAILED: Firebase database() service has not been registered.');
    };
    Object.defineProperty(FirebaseApp.prototype, "name", {
        /**
         * Returns the name of the FirebaseApp instance.
         *
         * @returns {string} The name of the FirebaseApp instance.
         */
        get: function () {
            this.checkDestroyed_();
            return this.name_;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FirebaseApp.prototype, "options", {
        /**
         * Returns the options for the FirebaseApp instance.
         *
         * @returns {FirebaseAppOptions} The options for the FirebaseApp instance.
         */
        get: function () {
            this.checkDestroyed_();
            return deep_copy_1.deepCopy(this.options_);
        },
        enumerable: true,
        configurable: true
    });
    /**
     * Deletes the FirebaseApp instance.
     *
     * @returns {Promise<void>} An empty Promise fulfilled once the FirebaseApp instance is deleted.
     */
    FirebaseApp.prototype.delete = function () {
        var _this = this;
        this.checkDestroyed_();
        this.firebaseInternals_.removeApp(this.name_);
        return Promise.all(Object.keys(this.services_).map(function (serviceName) {
            return _this.services_[serviceName].INTERNAL.delete();
        })).then(function () {
            _this.services_ = {};
            _this.isDeleted_ = true;
        });
    };
    /**
     * Returns the service instance associated with this FirebaseApp instance (creating it on demand
     * if needed).
     *
     * @param {string} serviceName The name of the service instance to return.
     * @return {FirebaseServiceInterface} The service instance with the provided name.
     */
    FirebaseApp.prototype.getService_ = function (serviceName) {
        this.checkDestroyed_();
        if (!(serviceName in this.services_)) {
            this.services_[serviceName] = this.firebaseInternals_.serviceFactories[serviceName](this, this.extendApp_.bind(this));
        }
        return this.services_[serviceName];
    };
    /**
     * Callback function used to extend an App instance at the time of service instance creation.
     */
    FirebaseApp.prototype.extendApp_ = function (props) {
        deep_copy_1.deepExtend(this, props);
    };
    /**
     * Throws an Error if the FirebaseApp instance has already been deleted.
     */
    FirebaseApp.prototype.checkDestroyed_ = function () {
        if (this.isDeleted_) {
            throw new Error("Firebase app named \"" + this.name_ + "\" has already been deleted.");
        }
    };
    return FirebaseApp;
}());
exports.FirebaseApp = FirebaseApp;
