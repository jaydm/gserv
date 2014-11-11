/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Lee Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.groovyrest.gserv.configuration

import org.groovyrest.gserv.*
import org.groovyrest.gserv.delegates.DefaultDelegates
import org.groovyrest.gserv.delegates.DelegatesMgr
import org.groovyrest.gserv.GServResource
import org.groovyrest.gserv.utils.LinkBuilder
import org.groovyrest.gserv.utils.StaticFileHandler
import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle

//@AutoClone(style = AutoCloneStyle.COPY_CONSTRUCTOR)
class HttpsConfig {
    def keyManagerAlgorithm = 'SunX509'
    def trustManagerAlgorithm = 'SunX509'
    def keyStoreFilePath = "/Users/lcollins/gserv.keystore"
    def keyStoreImplementation = "JKS"
    def password
    def sslProtocol
}

/**
 *
 * @author lcollins
 *
 * gServ Server Instance Configuration
 *
 */
@AutoClone(style = AutoCloneStyle.CLONE)
class GServConfig {

    private def _name = 'gserv App';
    private def _routes = [];
    def _staticRoots = [], _filters = [], _authenticator,
        _templateEngineName, bUseResourceDocs, _defaultPort
    def delegateTypeMap
    def delegateMgr
    def linkBuilder
    def _defaultResource
    StaticFileHandler _staticFileHandler = new StaticFileHandler()
    org.groovyrest.gserv.Matcher matcher = new Matcher()
    def serverIPs = []
    HttpsConfig _https

    GServConfig() {
        this.delegateManager(new DelegatesMgr(DefaultDelegates.getDelegates()))
    }

    def name() {
        _name
    }

    def name(nm) {
        _name = nm
    }

    def httpsConfig(HttpsConfig httpsConfig) {
        _https = httpsConfig
    }

    def httpsConfig() {
        _https
    }

    def https() {
        !!_https
    }

    def authenticator(authenticator) {
        this._authenticator = authenticator;
        this
    }

    def authenticator() { _authenticator }

    def delegateManager(delegateMgr) {
        this.delegateMgr = delegateMgr;
        this
    }

    def delegateManager() { this.delegateMgr }

    def linkBuilder(linkBuilder) {
        this.linkBuilder = linkBuilder;
        this
    }

    def linkBuilder() {
        this.linkBuilder = this.linkBuilder ?: new LinkBuilder();
        this.linkBuilder
    }

    def matchRoute(exchange) {
        matcher.matchRoute(_routes, exchange)
    }

    def requestMatched(exchange) {
        !!(matchRoute(exchange) || _staticFileHandler.resolveStaticResource(
                exchange.requestURI.path,
                _staticRoots,
                bUseResourceDocs))
    }

    /**
     * Sets the template engine for this configuration
     *
     * @param ten Template Engine Name
     *
     */
    def templateEngineName(ten) {
        _templateEngineName = ten;
        this
    }

    def templateEngineName() { _templateEngineName }

    /**
     *
     * @param b if true, resource docs will be scanned for static content requests
     *
     */
    def useResourceDocs(b) {
        bUseResourceDocs = b;
        this
    }

    boolean useResourceDocs() {
        bUseResourceDocs
    }

    /**
     * Sets the delegates to use for this configuration.
     *
     * @param dtm Map( delegateType -> delegate )
     *
     */
    def delegateTypeMap(dtm) {
        delegateTypeMap = dtm;
        this
    }

    /**
     *
     */
    def port(int p) {
        this._defaultPort = p;
        this
    }

    /**
     *
     */
    def port() {
        this._defaultPort
    }

    def addResource(GServResource resource) {
        resource.routes.each this.&addRoute
        this
    }

    def addResources(resources) {
        resources.each this.&addResource
        this
    }

    /**
     * Add routes to this Configuration
     *
     * @param List < Route >  List of Routes
     * @return GServConfig
     */
    def addRoutes(rlist) {
        routes(rlist);
    }

    def routes(rlist) {
        this._routes.addAll(rlist);
        this
    }

    /**
     * Add routes to this Configuration
     *
     * @param List < Route >  List of Routes
     * @return GServConfig
     */
    def addRoute(route) {
        this._routes.add(route);
        this
    }

    def routes() {
        this._routes;
    }

    def addServerIP(serverIp) {
        this.serverIPs.add(serverIp);
        this
    }

    /**
     * Add staticRoots to this Configuration
     *
     * @param List < String >  List of directories
     * @return List < String >
     */
    def addStaticRoots(roots) {
        this._staticRoots.addAll(roots);
        this
    }

    def staticRoots() {
        _staticRoots
    }

    /**
     *
     * @param filters List<org.groovyrest.gserv.filters.Filter>
     * @return List < org.groovyrest.gserv.filters.Filter >
     */
    def addFilter(filter) {
        this._filters.add(filter);
        this
    }
    /**
     *
     * @param filters List<org.groovyrest.gserv.filters.Filter>
     * @return List < org.groovyrest.gserv.filters.Filter >
     */
    def addFilters(filters) {
        this._filters.addAll(filters);
        this;
    }

    def filters() { _filters }

    def defaultResource(def defResource) {
        _defaultResource = defResource
    }

    def defaultResource() {
        _defaultResource
    }

    def applyHttpsConfig(cfgObj) {
        if (cfgObj.password) {
            def httpsCfg = new HttpsConfig()
            httpsCfg.password = cfgObj.password
            httpsCfg.keyManagerAlgorithm = cfgObj.keyManagerAlgorithm
            httpsCfg.keyStoreFilePath = cfgObj.keyStoreFilePath ?: (System.getProperty("user.home") + "/gserv.keystore")

            httpsCfg.keyStoreImplementation = cfgObj.keyStoreImplementation
            httpsCfg.trustManagerAlgorithm = cfgObj.trustManagerAlgorithm
            httpsCfg.sslProtocol = cfgObj.sslProtocol
            httpsConfig(httpsCfg)
        } else {
            throw new IllegalArgumentException("Password is required for HTTPS.")
        }

    }
}

