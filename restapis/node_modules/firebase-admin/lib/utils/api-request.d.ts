/*! firebase-admin v4.0.4
    https://firebase.google.com/terms/ */
/// <reference types="chai" />
import { Credential } from '../auth/credential';
/** Http method type definition. */
export declare type HttpMethod = 'GET' | 'POST';
/** API callback function type definition. */
export declare type ApiCallbackFunction = (data: Object) => void;
/**
 * Base class for handling HTTP requests.
 */
export declare class HttpRequestHandler {
    /**
     * Sends HTTP requests and returns a promise that resolves with the result.
     *
     * @param {string} host The HTTP host.
     * @param {number} port The port number.
     * @param {string} path The endpoint path.
     * @param {HttpMethod} httpMethod The http method.
     * @param {Object} [data] The request JSON.
     * @param {Object} [headers] The request headers.
     * @param {number} [timeout] The request timeout in milliseconds.
     * @return {Promise<Object>} A promise that resolves with the response.
     */
    sendRequest(host: string, port: number, path: string, httpMethod: HttpMethod, data?: Object, headers?: Object, timeout?: number): Promise<Object>;
}
/**
 * Class that extends HttpRequestHandler and signs HTTP requests with a service
 * credential access token.
 *
 * @param {Credential} credential The service account credential used to
 *     sign HTTP requests.
 * @constructor
 */
export declare class SignedApiRequestHandler extends HttpRequestHandler {
    private credential;
    constructor(credential: Credential);
    /**
     * Sends HTTP requests and returns a promise that resolves with the result.
     *
     * @param {string} host The HTTP host.
     * @param {number} port The port number.
     * @param {string} path The endpoint path.
     * @param {HttpMethod} httpMethod The http method.
     * @param {Object} data The request JSON.
     * @param {Object} headers The request headers.
     * @param {number} timeout The request timeout in milliseconds.
     * @return {Promise} A promise that resolves with the response.
     */
    sendRequest(host: string, port: number, path: string, httpMethod: HttpMethod, data: Object, headers: Object, timeout: number): Promise<Object>;
}
/**
 * Class that defines all the settings for the backend API endpoint.
 *
 * @param {string} endpoint The Firebase Auth backend endpoint.
 * @param {HttpMethod} httpMethod The http method for that endpoint.
 * @constructor
 */
export declare class ApiSettings {
    private endpoint;
    private httpMethod;
    private requestValidator;
    private responseValidator;
    constructor(endpoint: string, httpMethod?: HttpMethod);
    /** @return {string} The backend API endpoint. */
    getEndpoint(): string;
    /** @return {HttpMethod} The request HTTP method. */
    getHttpMethod(): HttpMethod;
    /**
     * @param {ApiCallbackFunction} requestValidator The request validator.
     * @return {ApiSettings} The current API settings instance.
     */
    setRequestValidator(requestValidator: ApiCallbackFunction): ApiSettings;
    /** @return {ApiCallbackFunction} The request validator. */
    getRequestValidator(): ApiCallbackFunction;
    /**
     * @param {ApiCallbackFunction} responseValidator The response validator.
     * @return {ApiSettings} The current API settings instance.
     */
    setResponseValidator(responseValidator: ApiCallbackFunction): ApiSettings;
    /** @return {ApiCallbackFunction} The response validator. */
    getResponseValidator(): ApiCallbackFunction;
}
