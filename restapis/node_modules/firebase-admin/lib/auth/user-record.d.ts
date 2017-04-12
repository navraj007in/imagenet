/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
/**
 * User metadata class that provides metadata information like user account creation
 * and last sign in time.
 *
 * @param {Object} response The server side response returned from the getAccountInfo
 *     endpoint.
 * @constructor
 */
export declare class UserMetadata {
    private lastSignedInAtInternal;
    private createdAtInternal;
    constructor(response: any);
    /** @return {Date} The user's last sign-in date. */
    readonly lastSignedInAt: Date;
    /** @return {Date} The user's account creation date. */
    readonly createdAt: Date;
    /** @return {Object} The plain object representation of the user's metadata. */
    toJSON(): Object;
}
/**
 * User info class that provides provider user information for different
 * Firebase providers like google.com, facebook.com, password, etc.
 *
 * @param {Object} response The server side response returned from the getAccountInfo
 *     endpoint.
 * @constructor
 */
export declare class UserInfo {
    private uidInternal;
    private displayNameInternal;
    private emailInternal;
    private photoURLInternal;
    private providerIdInternal;
    constructor(response: any);
    /** @return {string} The provider user id. */
    readonly uid: string;
    /** @return {string} The provider display name. */
    readonly displayName: string;
    /** @return {string} The provider email. */
    readonly email: string;
    /** @return {string} The provider photo URL. */
    readonly photoURL: string;
    /** @return {string} The provider Firebase ID. */
    readonly providerId: string;
    /** @return {Object} The plain object representation of the current provider data. */
    toJSON(): Object;
}
/**
 * User record class that defines the Firebase user object populated from
 * the Firebase Auth getAccountInfo response.
 *
 * @param {Object} response The server side response returned from the getAccountInfo
 *     endpoint.
 * @constructor
 */
export declare class UserRecord {
    private uidInternal;
    private emailInternal;
    private emailVerifiedInternal;
    private displayNameInternal;
    private photoURLInternal;
    private disabledInternal;
    private metadataInternal;
    private providerDataInternal;
    constructor(response: any);
    /** @return {string} The Firebase user id corresponding to the current user record. */
    readonly uid: string;
    /** @return {string} The primary email corresponding to the current user record. */
    readonly email: string;
    /** @return {boolean} Whether the primary email is verified. */
    readonly emailVerified: boolean;
    /** @return {string} The display name corresponding to the current user record. */
    readonly displayName: string;
    /** @return {string} The photo URL corresponding to the current user record. */
    readonly photoURL: string;
    /** @return {boolean} Whether the current user is disabled or not. */
    readonly disabled: boolean;
    /** @return {UserMetadata} The user record's metadata. */
    readonly metadata: UserMetadata;
    /** @return {UserInfo[]} The list of providers linked to the current record. */
    readonly providerData: UserInfo[];
    /** @return {Object} The plain object representation of the user record. */
    toJSON(): Object;
}
