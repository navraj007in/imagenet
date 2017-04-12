/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
import { Certificate } from './credential';
/**
 * Class for generating and verifying different types of Firebase Auth tokens (JWTs).
 */
export declare class FirebaseTokenGenerator {
    private certificate_;
    private publicKeys_;
    private publicKeysExpireAt_;
    constructor(certificate: Certificate);
    /**
     * Creates a new Firebase Auth Custom token.
     *
     * @param {string} uid The user ID to use for the generated Firebase Auth Custom token.
     * @param {Object} [developerClaims] Optional developer claims to include in the generated Firebase
     *                 Auth Custom token.
     * @return {Promise<string>} A Promise fulfilled with a Firebase Auth Custom token signed with a
     *                           service account key and containing the provided payload.
     */
    createCustomToken(uid: string, developerClaims?: Object): Promise<string>;
    /**
     * Verifies the format and signature of a Firebase Auth ID token.
     *
     * @param {string} idToken The Firebase Auth ID token to verify.
     * @return {Promise<Object>} A promise fulfilled with the decoded claims of the Firebase Auth ID
     *                           token.
     */
    verifyIdToken(idToken: string): Promise<Object>;
    /**
     * Returns whether or not the provided developer claims are valid.
     *
     * @param {Object} [developerClaims] Optional developer claims to validate.
     * @return {boolean} True if the provided claims are valid; otherwise, false.
     */
    private isDeveloperClaimsValid_(developerClaims?);
    /**
     * Fetches the public keys for the Google certs.
     *
     * @return {Promise<Object>} A promise fulfilled with public keys for the Google certs.
     */
    private fetchPublicKeys_();
}
