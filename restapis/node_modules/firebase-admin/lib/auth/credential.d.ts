/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
export declare class RefreshToken {
    clientId: string;
    clientSecret: string;
    refreshToken: string;
    type: string;
    static fromPath(path: string): RefreshToken;
    constructor(json: Object);
}
/**
 * A struct containing the fields necessary to use service-account JSON credentials.
 */
export declare class Certificate {
    projectId: string;
    privateKey: string;
    clientEmail: string;
    static fromPath(path: string): Certificate;
    constructor(json: Object);
}
/**
 * Interface for Google OAuth 2.0 access tokens.
 */
export declare type GoogleOAuthAccessToken = {
    access_token: string;
    expires_in: number;
};
/**
 * Implementation of Credential that uses a service account certificate.
 */
export declare class CertCredential implements Credential {
    private certificate_;
    constructor(serviceAccountPathOrObject: string | Object);
    getAccessToken(): Promise<GoogleOAuthAccessToken>;
    getCertificate(): Certificate;
    private createAuthJwt_();
}
/**
 * Interface for things that generate access tokens.
 */
export interface Credential {
    getAccessToken(): Promise<GoogleOAuthAccessToken>;
    getCertificate(): Certificate;
}
/**
 * Implementation of Credential that gets access tokens from refresh tokens.
 */
export declare class RefreshTokenCredential implements Credential {
    private refreshToken_;
    constructor(refreshTokenPathOrObject: string | Object);
    getAccessToken(): Promise<GoogleOAuthAccessToken>;
    getCertificate(): Certificate;
}
/**
 * Implementation of Credential that gets access tokens from the metadata service available
 * in the Google Cloud Platform. This authenticates the process as the default service account
 * of an App Engine instance or Google Compute Engine machine.
 */
export declare class MetadataServiceCredential implements Credential {
    getAccessToken(): Promise<GoogleOAuthAccessToken>;
    getCertificate(): Certificate;
}
/**
 * ApplicationDefaultCredential implements the process for loading credentials as
 * described in https://developers.google.com/identity/protocols/application-default-credentials
 */
export declare class ApplicationDefaultCredential implements Credential {
    private credential_;
    constructor();
    getAccessToken(): Promise<GoogleOAuthAccessToken>;
    getCertificate(): Certificate;
    getCredential(): Credential;
}
