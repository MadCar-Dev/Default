/* 
    Collection of Library Functions

*/

 // Retrieves the Token name form the seleected tokens
    function getTokenName(selected) {
        
        var name = '';
        if(selected && selected.length === 1) {
            var selected_id = selected[0]._id;
            var token = getObj('graphic',selected_id);
           
            if(token) {

                name = token.get('name');
            }
        }
        return name;
    }
    
     function isTokenStatusSet(selected,status) {
         
        if(selected && selected.length === 1) {
            var selected_id = selected[0]._id;
            var token = getObj('graphic',selected_id);
            if(token) {
                let statusObj = libTokenMarkers.getStatus(status);
                return (false !== token.get(`status_${statusObj.getTag()}`));
            }
        }
        return false;
    }
    
    function getTokenControlledby(selected) {
        
        var controlledby = '';
        if(selected && selected.length === 1) {
           var selected_id = selected[0]._id;
           var token = getObj('graphic',selected_id);
          
            if(token) {
               controlledby = token.get('controlledby');
                if (controlledby !='all'){
                    var represents = token.get('represents');
                    
                    //use the character represents not the controlled by
                    if (represents){
                        var charcater = getObj('character',represents);
                        controlledby = charcater.get('controlledby');
                       
                    }
                } else {
                    controlledby = '';
                }
            }
        }
        return controlledby;
    }
    
     function playerHasCard(playerId, cardid) {
        
       let hand = findObjs({                              
                _parentid: playerId,                              
                _type: "hand",                          
                })[0];
        let cards = hand.get("currentHand").split(",");
        return cards.includes(cardid);
    }


/* 
 * Roll20: https://app.roll20.net/users/6205674/angelo
 * Github: https://github.com/ocangelo/roll20/WildHelpers.js
*/
/*jshint -W083 */

class WildUtils {
    constructor(apiName, isDebug = false) {
        this.APINAME = apiName || "API";
        this.VERSION = "1.3.4";

        this.debugEnable(isDebug);
    }

    debugEnable(enable = true) {
        this.DEBUG = enable;
        this.DEBUG_CACHE = null;
        this.DEBUG_IMMEDIATE = true;
        //this.DEBUG_IMMEDIATE = immediate;
    }

    debugFlush(msg = null) {
        if (this.DEBUG)
        {
            if (msg !== null)
                this.DEBUG_CACHE = ((this.DEBUG_CACHE !== null) ? (this.DEBUG_CACHE + "<br>") : "") + msg;

            if(this.DEBUG_CACHE !== null)
            {
                this.chat(this.DEBUG_CACHE);
                this.DEBUG_CACHE = null;
            }
        }
    }

    debugChat(msg, cacheMsg = null) {
        if (this.DEBUG && msg !== null)
        {
            if (this.DEBUG_IMMEDIATE)
            {
                this.chat(msg);
            }
            else if (cacheMsg === null)
            {
                if (this.DEBUG_CACHE == null)
                    this.chat(msg);
                else
                    this.DEBUG_CACHE += "<br>" + msg;
            }
            else 
            {
                if (cacheMsg === true)
                {
                    this.DEBUG_CACHE = ((this.DEBUG_CACHE !== null) ? (this.DEBUG_CACHE + "<br>") : "") + msg;
                }
                else 
                {
                    if (this.DEBUG_CACHE !== null)
                    {
                        this.debugFlush();
                    }

                    this.chat(msg);
                }
            }
        }
    }

    chat(msg, callback = null, settings = {noarchive:true}) {
        sendChat(this.APINAME, "/w gm " + msg, callback, settings);
    }
    
    chatAs(characterId, msg, callback = null, settings = {noarchive:true}) {
        sendChat("character|" + characterId, msg, callback, settings);
    }

    chatToPlayer(who, msg, callback = null, settings = {noarchive:true}) {
       sendChat(this.APINAME, "/w " + who + " " + msg, callback, settings);
       this.chat("chatToPlayer: " + who + ", msg: " + msg);
    }

    chatError(msg, callback = null, settings = {noarchive:true}) {
       sendChat(this.APINAME, "/w gm ERROR: " + msg, callback, settings);
    }

    chatErrorToPlayer(who, msg, callback = null, settings = {noarchive:true}) {
       sendChat(this.APINAME, "/w " + who + " ERROR: " + msg, callback, settings);
       this.chatError("chatErrorToPlayer: " + who + ", msg: " + msg);
    }

    debugLog(msg)
    {
        if (this.DEBUG)
        {
            log(this.APINAME + ": " + msg);
        }
    }
    
    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // returns:
    // 1: versionA > versionB
    // 0: versionA == versionB
    // -1: versionA < versionB
    compareVersion(versionA, versionB) {
        if (typeof versionA !== 'object') { versionA = versionA.toString().split('.'); }
        if (typeof versionB !== 'object') { versionB = versionB.toString().split('.'); }

        const maxLen = Math.max(versionA.length,versionB.length);
        for (let i=0; i< maxLen; i++)
        {
            if (versionA[i] == undefined) { versionA[i]=0; }
            if (versionB[i] == undefined) { versionB[i]=0; }

            if (Number(versionA[i]) > Number(versionB[i]))
            {
                return 1;
            }
            else if (Number(versionA[i]) < Number(versionB[i]))
            {
                return -1;
            }
        }

        return 0;
    }

    regExEscape(str) {
      return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }

    getCleanImgsrc(imgsrc) {
        let parts = imgsrc.match(/(.*\/images\/.*)(thumb|med|original|max)([^\?]*)(\?[^?]+)?$/);
        if(parts) {
            return parts[1]+'thumb'+parts[3]+(parts[4]?parts[4]:`?${Math.round(Math.random()*9999999)}`);
        }
        return "";
    } 

    sortByKey(unordered) {
        let ordered = {};
        _.each(Object.keys(unordered).sort(function(a, b){return a.toLowerCase().localeCompare(b.toLowerCase());}), (key) => {
            ordered[key] = unordered[key];
        });

        return ordered;
    }

    getResourceAttribute(charId, name, caseSensitive) {
        const resRegEx = new RegExp(/^((repeating_resource_\-(?:\w+))|class|other)_resource(_left|_right)?(_name)?$/);
        const nameRegEx = new RegExp('^' + this.regExEscape(name) + '$', caseSensitive ? '' : 'i');
        let attrId = null;

        name = name.toLowerCase();
        let attrs = _.chain(findObjs({type:'attribute', characterid:charId}))
            .map((a) =>
                {
                    let o = {attr: a, match: a.get('name').match(resRegEx)};
                    return o;
                })

            // we only want _left|_right if it's a repeating_resource, javascript cannot use ?(2) in regex
            .filter((o) => o.match && ((o.match[2] !== null) == (o.match[3] !== null)))
            .filter((o) => 
                {
                    // if we matched "_name" we have to check if we found our target attribute
                    if (!attrId && o.match[4] && o.attr.get("current").match(nameRegEx))
                    {
                        attrId = { id : o.match[1], pos : o.match[3] };
                    }

                    // keep everything that doesn't have a _name at the end
                    return !o.match[4];
                })
            .value();

        if (attrId)
        {
            for (let i = attrs.length - 1; i >= 0; --i)
            { 
                if (attrs[i].match[1] == attrId.id && attrs[i].match[3] == attrId.pos)
                {
                    return attrs[i].attr;
                }
            }
        }

        return null;
    }

    setAttribute(characterId, attrName, attrCurrent, attrMax = null) {
        let targetAttr = findObjs({_type: "attribute", name: attrName, _characterid: characterId})[0];
        if (targetAttr)
        {
            targetAttr.set('current', attrCurrent);
            if (attrMax)
                targetAttr.set('max', attrMax);
        }
        else
        {
            targetAttr = createObj('attribute', {
                characterid: characterId,
                name: attrName,
                current: attrCurrent,
                max: (attrMax ? attrMax : "")
            });
        }
    }

    copyAttribute(fromId, fromAttrName, toId, toAttrName, defaultValue = null) {
        let fromAttr = findObjs({type: 'attribute', characterid: fromId, name: fromAttrName})[0];
        if (!fromAttr && !defaultValue)
        {
            this.chatError("Cannot copy missing attribute " + fromAttrName + " from character " + fromId);
            return;
        }

        let fromAttrCurrent = fromAttr ? fromAttr.get("current") : defaultValue;
        let toAttr = findObjs({_type: "attribute", name: toAttrName, _characterid: toId})[0];
        if (!toAttr) {
            this.debugChat("copyAttribute: creating " + toAttrName + ", value = " + fromAttrCurrent);
            createObj('attribute', {
                characterid: toId,
                name: toAttrName,
                current: fromAttrCurrent,
                max: fromAttr ? fromAttr.get("max") : null
            });
        }
        else
        {
            this.debugChat("copyAttribute: setting " + toAttrName + ", value = " + fromAttrCurrent);
            toAttr.set("current", fromAttrCurrent);
        }
    }

    isProficient(charId, attrName) {
        let attr = findObjs({_type: "attribute", name: attrName, _characterid: charId})[0];
        if(attr)
        {
            attr = attr.get("current");

            return attr && attr.indexOf("@{pb}") >=0;
        }

        return false;
    }

    getCharactersWithAttr(attributeName) {
        return _.chain(
            filterObjs((o)=>{
                return (o.get('type')==='attribute' && o.get('name') === attributeName);
            }))

        .reduce((m,o)=>{
            let obj = {};
            obj.cid = o.get('characterid');
            obj.char = getObj('character', obj.cid);
            if (!_.isUndefined(obj.char))
            {
                obj[attributeName] = o;
                m.push(obj);
            }

            return m;
        },[])

        .value();
    }

    getCharactersWithAttrValue(attribute, value) {
        return (this.getCharactersWithAttrByName(attribute) || {}).filter( (o)=> {return o[attribute].get('current') == value; } );
    }

    getPCs() {
        return this.getCharactersWithAttr("ideals");
    }

    getNPCs() {
        return this.getCharactersWithAttr("npc");
    }

    getPCNames() {
        return this.getPCs().reduce((m,o)=>{m.push(o.char.get('name')); return m; }, []);             
    }

    getNPCNames() {
        return this.getNPCs().reduce((m,o)=>{m.push(o.char.get('name')); return m; }, []);
    }

    // finds the folder 'name' anywhere in the journal
    findNestedFolder(folderData, folderName) {
        folderName = folderName.toLowerCase();

        let folderStack = [folderData];
        let currFolder = folderStack.shift();
        while (currFolder)
        {
            let obj;
            do 
            {
                obj = currFolder.shift();
                if (obj && _.isObject(obj))
                {
                    if (obj.n.toLowerCase() === folderName)
                    {
                        return obj;
                    }
                    else
                    {
                        folderStack.push(obj.i);
                    }
                }
            }
            while (currFolder.length > 0);

            currFolder = folderStack.shift();
        }

        return null;
    }

    // finds the folder fullpath in the journal
    findFolder(folderData, fullpath) {
        let currFolder = folderData;

        fullpath = fullpath.replace('\\', '/');
        
        let paths = fullpath.split('/');
        let currPath = paths.shift();
        if(fullpath.startsWith('/'))
        {
            currPath = paths.shift();
        }

        while (currFolder)
        {

            let found = false;
            let obj;
            do 
            {
                obj = currFolder.shift();
                if (obj && _.isObject(obj))
                {
                    if (obj.n.toLowerCase() === currPath.toLowerCase()) {
                        found = true;
                        break;
                    }
                }
            }
            while (currFolder.length > 0);

            if (found)
            {
                currPath = paths.shift();
                if (!currPath)
                {
                    return obj;
                }
                else
                {
                    currFolder = obj.i;
                }
            }
            else
                return null;
        }

        return null;
    }


    findCharactersInFolder(folder, findInSubfolders = false) {
        folder = folder.replace("\\","/");
        let folderData = this.findFolder(JSON.parse(Campaign().get('journalfolder')), folder);
        
        if (folderData)
        {
            if (findInSubfolders)
            {
                let charactersInFolder = [];
                let folderStack = [];
                let currFolder = folderData.i;
                while (currFolder)
                {
                    _.each(currFolder, function(obj) {
                        if(obj)
                        {
                            if(_.isString(obj))
                            {
                                let char = getObj('character', obj);
                                if (char)
                                    charactersInFolder.push(char);
                            }
                            else if(_.isObject(obj))
                            {
                                folderStack.push(obj.i);
                            }
                        }
                    });                            

                    currFolder = folderStack.shift();
                }

                return charactersInFolder;
            }
            else
            {
                return  _.chain(folderData.i)
                    .filter(function(obj) { return _.isString(obj); })
                    .map(function(id) { return getObj('character', id); })
                    .reject(function(char) { return !char; })
                    .value();
            }
        }
        else
        {
            this.chatError("Cannot find folder: " + folder);
        }

        return null;
    }

    async getDefaultToken(character) {
        let token = null;
        let maxTimeout = 3000;

        // get token image
        character.get('defaulttoken', function(defaultToken) {
            token = defaultToken ? defaultToken : "";
        });

        while (maxTimeout > 0 && !token)
        {
            await this.sleep(50);
            maxTimeout -= 50;
        }

        return token && token.trim() !== "" ? JSON.parse(token) : null;
    }

    async getDefaultTokenImage(character) {
        let img = null;

        // get token image
        await this.getDefaultToken(character).then((token) => {
            img = token ? this.getCleanImgsrc(token.imgsrc) : "";
        });

        return img;
    }

    async getDefaultTokenSize(character) {
        let w = 0;
        let h = 0;

        await this.getDefaultToken(character).then((token) => {
            if (token)
            {
                w = token.width;
                h = token.height;
            }
        });

        return { "width" : w, "height" : h };
    }

    async duplicateCharacter(targetCharacter, newCharacterName) {
        if (!targetCharacter)
        {
            this.chatError("WildUtils::duplicateCharacter: trying to duplicate an invalid character");
            return null;
        }

        let targetCharacterName = targetCharacter.get("name");
        let targetCharId = targetCharacter.get("_id");

        let errorMsgHeader = "WildUtils::duplicateCharacter (" + targetCharacterName + " -> " + newCharacterName + "): ";

        const jsonObj = (o) => JSON.parse(JSON.stringify(o));

        // create new character
        let newCharacterData = jsonObj(targetCharacter);
        delete newCharacterData._defaulttoken;
        delete newCharacterData._id;
        newCharacterData.name = newCharacterName;
        newCharacterData.avatar = this.getCleanImgsrc(targetCharacter.get("avatar"));

        let newCharacter = createObj('character', newCharacterData);
        let newCharacterId = newCharacter.get("_id");

        // find character token and make a copy
        let targetCharacterToken = null;
        let newCharacterToken = null;
        let tokenLinks = [null, null, null];
           
        await this.getDefaultToken(targetCharacter).then( (token) => { targetCharacterToken = token; });
        if (targetCharacterToken)
        {
            // make a copy of the data and create a new off screen temporary graphic token
            let newCharacterTokenData = jsonObj(targetCharacterToken);

            newCharacterTokenData.imgsrc = this.getCleanImgsrc(newCharacterTokenData.imgsrc);
            if(newCharacterTokenData.imgsrc == "")
            {
                newCharacterTokenData.imgsrc = this.getCleanImgsrc(targetCharacter.get("avatar"));
                if(newCharacterTokenData.imgsrc == "")
                {
                    this.chatError(errorMsgHeader + "cannot find image on either token or avatar; if it's using a marketplace link the image needs to be re-uploaded into the library and set on the target character as either token or avatar image");
                    newCharacter.remove();
                    return null;
                }
            }

            // graphic token data setup
            delete newCharacterTokenData._id;
            newCharacterTokenData.represents = newCharacterId;
            newCharacterTokenData._pageid = Campaign().get("playerpageid");
            newCharacterTokenData.layer = "gmlayer";
            newCharacterTokenData.left = -500;
            newCharacterTokenData.top = -500;

            // cache link info
            for (let i = 0; i < 3; ++i)
            {
                let barName = "bar" + (i + 1).toString();
                let linkId = newCharacterTokenData[barName + "_link"];
                if (linkId && linkId !== "")
                {
                    delete newCharacterTokenData[barName + "_link"];

                    let linkAttr = getObj("attribute", linkId);
                    if (linkAttr)
                    {
                        let tmpTokenData = {};
                        tmpTokenData.id = linkId;
                        
                        let val = newCharacterTokenData[barName + "_value"];                            
                        if (!_.isUndefined(val))
                        {
                            tmpTokenData.value = val;
                        }
                        else
                        {
                            tmpTokenData.value = linkAttr.get("current");
                            newCharacterTokenData[barName + "_value"] = tmpTokenData.value;
                        }

                        val = newCharacterTokenData[barName + "_max"];
                        if (!_.isUndefined(val))
                        {
                            tmpTokenData.max = val;
                        }
                        else
                        {
                            tmpTokenData.max = linkAttr.get("max");
                            newCharacterTokenData[barName + "_max"] = tmpTokenData.max;
                        }

                        tokenLinks[i] = tmpTokenData;
                    }
                }
            }

            // create new token
            newCharacterToken = createObj("graphic", newCharacterTokenData);
            if(!newCharacterToken)
            {
                this.chatError(errorMsgHeader + "cannot create a new graphic token");
                newCharacter.remove();
                return null;
            }
        }

        // copy attributes
        _.each(findObjs({type:'attribute', characterid: targetCharId}), (attr) => {
            let attrData = jsonObj(attr);
            let oldAttrId = attrData._id;

            delete attrData._id;
            delete attrData._type;
            attrData._characterid = newCharacterId;
            let newAttr = createObj('attribute', attrData);

            // check if we need to link the token to the new attribute
            if (newCharacterToken)
            {
                for (let linkIndex = 0; linkIndex < 3; ++linkIndex)
                {
                    if (tokenLinks[linkIndex] !== null && tokenLinks[linkIndex].id ==  oldAttrId)
                    {
                        let barName = "bar" + (linkIndex + 1).toString();
                        newCharacterToken.set(barName + "_link", newAttr.id);
                        newCharacterToken.set(barName + "_max", tokenLinks[linkIndex].max);
                        newCharacterToken.set(barName + "_value", tokenLinks[linkIndex].value);
                        break;
                    }
                }
            }
        });

        // copy abilities
        _.each(findObjs({type:'ability', characterid: targetCharId}), (ability) => {
            let abilityData = jsonObj(ability);
            delete abilityData._id;
            delete abilityData._type;
            abilityData._characterid = newCharacterId;
            createObj('ability', abilityData);
        });

        targetCharacter.get("bio", function(bio) {
            if (bio && typeof bio === 'string' && bio.trim() !== "" && bio !== "null")
                newCharacter.set('bio', bio); 
        });

        targetCharacter.get("gmnotes", function(gmnotes) {
            if (gmnotes && typeof gmnotes === 'string' && gmnotes.trim() !== "" && gmnotes !== "null")
                newCharacter.set('gmnotes', gmnotes); 
        });

        if (newCharacterToken)
        {
            // this will make a snapshot of the current characterToken
            setDefaultTokenForCharacter(newCharacter, newCharacterToken);
            newCharacterToken.remove();
        }

        this.debugChat("Duplicated: " + targetCharacterName + " into " + newCharacterName);
        return newCharacter;
    }
}

class WildMenu {
    constructor()
    {
        this.MENU_STYLE = "overflow: hidden; background-color: #fff; border: 1px solid #000; padding: 5px; border-radius: 5px; ";
        this.BUTTON_STYLE = "background-color: #1b70e0; border: 1px solid #292929; border-radius: 3px; padding: 5px; color: #fff; text-align: center; ";
        this.LIST_STYLE = "list-style: none; padding: 0; margin: 0; margin-bottom: 10px; overflow:hidden; ";
        this.ITEM_STYLE = "overflow:hidden; padding-bottom: 5px; ";
    }

    makeTitle(title, title_tag) {
        title_tag = (title_tag && title_tag !== '') ? title_tag : 'h3';
        return '<' + title_tag + ' style="margin-bottom: 10px;">' + title + '</' + title_tag+'>';
    }

    makeButton(title, href, addStyle, alt) {
        return '<a style="'+ this.BUTTON_STYLE + addStyle + '" href="' + href + '" title="' + (alt || href) + '">' + title + '</a>';
    }

    makeRightButton(buttonName, href, addStyle, alt) {
        return this.makeButton(buttonName, href,  "float: right; " + addStyle, alt);
    }

    makeLabel(itemName, addStyle) {
        return '<span style="' + addStyle + '">' + itemName + '</span> ';
    }

    makeLabelValue(name, value, defaultValue = '', addStyle = null)
    {
        return this.makeLabel(name + ": &lt;" + (value || defaultValue) + "&gt;", addStyle);
    }

    makeLabelComment(itemName, addStyle) {
        return this.makeLabel(itemName, "font-size: 80%; padding-left: 10px; padding-bottom: 5px; " + addStyle);
    }

    makeList(items, addListStyle, addItemStyle) {
        let list = '<ul style="' + this.LIST_STYLE + addListStyle + '">';
        items.forEach((item) => {
            list += '<li style="' + this.ITEM_STYLE + addItemStyle + '">' + item + '</li>';
        });
        list += '</ul>';
        return list;
    }

    showMenu(who, contents, title, settings) {
        settings = settings || {};
        settings.whisper = (typeof settings.whisper === 'undefined') ? '/w gm ' : '/w ' + settings.whisper + ' ';
        title = (title && title != '') ? this.makeTitle(title, settings.title_tag || '') : '';
        sendChat(who, settings.whisper + '<div style="' + this.MENU_STYLE + '">' + title + contents + '</div>', null, {noarchive:true});
    }
}


    copyAttribute(fromId, fromAttrName, toId, toAttrName, defaultValue = null) {
        this.debugChat("copyAttribute: find attr on src");
        let fromAttr = findObjs({type: 'attribute', characterid: fromId, name: fromAttrName})[0];
        if (!fromAttr && !defaultValue)
        {
            this.chatError("Cannot copy missing attribute " + fromAttrName + " from character " + fromId);
            return;
        }

        let fromAttrCurrent = fromAttr ? fromAttr.get("current") : defaultValue;
        this.debugChat("copyAttribute: find attr on dest " + toAttrName);
        let toAttr = findObjs({_type: "attribute", name: toAttrName, _characterid: toId})[0];
        if (!toAttr) {
            this.debugChat("copyAttribute: creating attr " + toAttrName);
            createObj('attribute', {
                characterid: toId,
                name: toAttrName,
                current: fromAttrCurrent,
                max: fromAttr ? fromAttr.get("max") : null
            });
        }
        else
        {
            this.debugChat("copyAttribute: set current value " + fromAttrCurrent + " on existing attr ");
            toAttr.set("current", fromAttrCurrent);
        }
    }