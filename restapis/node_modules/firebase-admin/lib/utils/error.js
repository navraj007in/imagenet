/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var deep_copy_1 = require('../utils/deep-copy');
/**
 * Firebase error code structure. This extends Error.
 *
 * @param {ErrorInfo} errorInfo The error information (code and message).
 * @constructor
 */
var FirebaseError = (function (_super) {
    __extends(FirebaseError, _super);
    function FirebaseError(errorInfo) {
        _super.call(this, errorInfo.message);
        this.errorInfo = errorInfo;
    }
    Object.defineProperty(FirebaseError.prototype, "code", {
        /** @return {string} The error code. */
        get: function () {
            return this.errorInfo.code;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FirebaseError.prototype, "message", {
        /** @return {string} The error message. */
        get: function () {
            return this.errorInfo.message;
        },
        enumerable: true,
        configurable: true
    });
    /** @return {Object} The object representation of the error. */
    FirebaseError.prototype.toJSON = function () {
        return {
            code: this.code,
            message: this.message,
        };
    };
    return FirebaseError;
}(Error));
exports.FirebaseError = FirebaseError;
/**
 * Firebase Auth error code structure. This extends FirebaseError.
 *
 * @param {ErrorInfo} info The error code info.
 * @param {string} [message] The error message. This will override the default
 *     message if provided.
 * @constructor
 */
var FirebaseAuthError = (function (_super) {
    __extends(FirebaseAuthError, _super);
    function FirebaseAuthError(info, message) {
        // Override default message if custom message provided.
        _super.call(this, { code: 'auth/' + info.code, message: message || info.message });
    }
    /**
     * Creates the developer facing error corresponding to the backend error code.
     *
     * @param {string} serverErrorCode The server error code.
     * @param {string} [message] The error message. The default message is used
     *     if not provided.
     * @return {FirebaseAuthError} The corresponding developer facing error.
     */
    FirebaseAuthError.fromServerError = function (serverErrorCode, message) {
        // If not found, default to internal error.
        var clientCodeKey = AUTH_SERVER_TO_CLIENT_CODE[serverErrorCode] || 'INTERNAL_ERROR';
        var error = deep_copy_1.deepCopy(AuthClientErrorCode[clientCodeKey]);
        error.message = message || error.message;
        return new FirebaseAuthError(error);
    };
    return FirebaseAuthError;
}(FirebaseError));
exports.FirebaseAuthError = FirebaseAuthError;
/** @const {TODO} Auth client error codes and their default messages. */
var AuthClientErrorCode = (function () {
    function AuthClientErrorCode() {
    }
    AuthClientErrorCode.INVALID_ARGUMENT = {
        code: 'argument-error',
        message: 'Invalid argument provided.',
    };
    AuthClientErrorCode.EMAIL_ALREADY_EXISTS = {
        code: 'email-already-exists',
        message: 'The email address is already in use by another account.',
    };
    AuthClientErrorCode.INTERNAL_ERROR = {
        code: 'internal-error',
        message: 'An internal error has occurred.',
    };
    AuthClientErrorCode.INVALID_CREDENTIAL = {
        code: 'invalid-credential',
        message: 'Invalid credential object provided.',
    };
    AuthClientErrorCode.INVALID_DISABLED_FIELD = {
        code: 'invalid-disabled-field',
        message: 'The disabled field must be a boolean.',
    };
    AuthClientErrorCode.INVALID_DISPLAY_NAME = {
        code: 'invalid-display-name',
        message: 'The displayName field must be a valid string.',
    };
    AuthClientErrorCode.INVALID_EMAIL_VERIFIED = {
        code: 'invalid-email-verified',
        message: 'The emailVerified field must be a boolean.',
    };
    AuthClientErrorCode.INVALID_EMAIL = {
        code: 'invalid-email',
        message: 'The email address is improperly formatted.',
    };
    AuthClientErrorCode.INVALID_PASSWORD = {
        code: 'invalid-password',
        message: 'The password must be a string with at least 6 characters.',
    };
    AuthClientErrorCode.INVALID_PHOTO_URL = {
        code: 'invalid-photo-url',
        message: 'The photoURL field must be a valid URL.',
    };
    AuthClientErrorCode.INVALID_SERVICE_ACCOUNT = {
        code: 'invalid-service-account',
        message: 'The service account provided is invalid.',
    };
    AuthClientErrorCode.INVALID_UID = {
        code: 'invalid-uid',
        message: 'The uid must be a non-empty string with at most 128 characters.',
    };
    AuthClientErrorCode.MISSING_UID = {
        code: 'missing-uid',
        message: 'A uid identifier is required for the current operation.',
    };
    AuthClientErrorCode.OPERATION_NOT_ALLOWED = {
        code: 'operation-not-allowed',
        message: 'The given sign-in provider is disabled for this Firebase project. ' +
            'Enable it in the Firebase console, under the sign-in method tab of the ' +
            'Auth section.',
    };
    AuthClientErrorCode.PROJECT_NOT_FOUND = {
        code: 'project-not-found',
        message: 'No Firebase project was found for the provided credential.',
    };
    AuthClientErrorCode.UID_ALREADY_EXISTS = {
        code: 'uid-already-exists',
        message: 'The user with the provided uid already exists.',
    };
    AuthClientErrorCode.USER_NOT_FOUND = {
        code: 'user-not-found',
        message: 'There is no user record corresponding to the provided identifier.',
    };
    return AuthClientErrorCode;
}());
exports.AuthClientErrorCode = AuthClientErrorCode;
;
/** @const {ServerToClientCode} Auth server to client enum error codes. */
var AUTH_SERVER_TO_CLIENT_CODE = {
    // Project not found.
    CONFIGURATION_NOT_FOUND: 'PROJECT_NOT_FOUND',
    // uploadAccount provides an email that already exists.
    DUPLICATE_EMAIL: 'EMAIL_EXISTS',
    // uploadAccount provides a localId that already exists.
    DUPLICATE_LOCAL_ID: 'UID_ALREADY_EXISTS',
    // setAccountInfo email already exists.
    EMAIL_EXISTS: 'EMAIL_ALREADY_EXISTS',
    // Invalid email provided.
    INVALID_EMAIL: 'INVALID_EMAIL',
    // Invalid service account.
    INVALID_SERVICE_ACCOUNT: 'INVALID_SERVICE_ACCOUNT',
    // No localId provided (deleteAccount missing localId).
    MISSING_LOCAL_ID: 'MISSING_UID',
    // Empty user list in uploadAccount.
    MISSING_USER_ACCOUNT: 'MISSING_UID',
    // Password auth disabled in console.
    OPERATION_NOT_ALLOWED: 'OPERATION_NOT_ALLOWED',
    // Project not found.
    PROJECT_NOT_FOUND: 'PROJECT_NOT_FOUND',
    // User on which action is to be performed is not found.
    USER_NOT_FOUND: 'USER_NOT_FOUND',
    // Password provided is too weak.
    WEAK_PASSWORD: 'INVALID_PASSWORD',
};
