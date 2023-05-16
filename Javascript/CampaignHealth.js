var API_Meta = API_Meta || {};
API_Meta.CampaignHealth = { offset: Number.MAX_SAFE_INTEGER, lineCount: -1 };
{
  try { throw new Error(''); } catch (e) { API_Meta.CampaignHealth.offset = (parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/, '$1'), 10) - (4)); }
}

on('ready', () => {
  const version = '0.0.02';
  log('Campaign Health ' + version + ' is ready! --offset '+ API_Meta.CampaignHealth.offset);
  log(' To start using the mod Campaign Health, in the chat window enter `!campaignhealth`');
  API_Meta.CampaignHealth.version = version;            


on('chat:message', async (msg_orig) => {
  let msg = _.clone(msg_orig);
  if (!/^!campaignhealth/i.test(msg.content)) {
      return;
  }
  buildCampaignHealthReports(msg.content);
});
      
function buildCampaignHealthReports(msg_content) {

  // ============================================
  //      PRESENTATION
  // ============================================
  /*
      - color management
      - CSS
      - HTML
      - messaging
      - help construction
  */

  // COLOR MANAGEMENT ===========================
  const getAltColor = (primarycolor, fade = .35) => {

      let pc = hexToRGB(`#${primarycolor.replace(/#/g, '')}`);
      let sc = [0, 0, 0];

      for (let i = 0; i < 3; i++) {
          sc[i] = Math.floor(pc[i] + (fade * (255 - pc[i])));
      }

      return RGBToHex(sc[0], sc[1], sc[2]);
  };
  const RGBToHex = (r, g, b) => {
      r = r.toString(16);
      g = g.toString(16);
      b = b.toString(16);

      if (r.length === 1)
          r = "0" + r;
      if (g.length === 1)
          g = "0" + g;
      if (b.length === 1)
          b = "0" + b;

      return "#" + r + g + b;
  };
  const getTextColor = (h) => {
      h = `#${h.replace(/#/g, '')}`;
      let hc = hexToRGB(h);
      return (((hc[0] * 299) + (hc[1] * 587) + (hc[2] * 114)) / 1000 >= 128) ? "#000000" : "#ffffff";
  };
  const hexToRGB = (h) => {
      let r = 0, g = 0, b = 0;

      // 3 digits
      if (h.length === 4) {
          r = "0x" + h[1] + h[1];
          g = "0x" + h[2] + h[2];
          b = "0x" + h[3] + h[3];
          // 6 digits
      } else if (h.length === 7) {
          r = "0x" + h[1] + h[2];
          g = "0x" + h[3] + h[4];
          b = "0x" + h[5] + h[6];
      }
      return [+r, +g, +b];
  };
  const validateHexColor = (s, d = defaultThemeColor1) => {
      let colorRegX = /(^#?[0-9A-Fa-f]{6}$)|(^#?[0-9A-Fa-f]{3}$)|(^#?[0-9A-Fa-f]{6}\d{2}$)/i;
      return '#' + (colorRegX.test(s) ? s.replace('#', '') : d);
  };

  // CSS ========================================
  const defaultThemeColor1 = '#521e10';
  const defaultThemeColor2 = '#84c6a6';
  const defaultThemeColor3 = '#ffc286';
  const defaultThemeColor4 = '#fff1af';
  const defaultbgcolor = "#ce0f69";

  const defaultdivCSS = {
      "border-radius": `2px`,
      "border": `1px solid #000000`,
      "background-color": '#00000000',
      "overflow": `hidden`
  };
  const defaulttableCSS = {
      'width' : '100%',
      'margin' : '0 auto',
      "border-collapse": 'collapse',
      "font-size": '11px',
  };
  const defaultimgCSS = {};
  const defaultpCSS = {};
  const defaultaCSS = {};
  const defaulth1CSS = {};
  const defaulth2CSS = {};
  const defaulth3CSS = {
    'color': '#521E10'};
  const defaulth4CSS = {
      'color': '#521E10'};
  const defaulth5CSS = {};
  const defaultthCSS = {
      "border-bottom": `1px solid #000000`,
      "font-weight": `bold`,
      "text-align": `center`,
      "line-height": `22px`
  };
  const defaulttrCSS = {};
  const defaulttdCSS = {
      'padding' : '2px',
      'min-width': '10px'
  };
  const defaultcodeCSS = {};

  const defaultMessageHeaderCSS = {
      'border-bottom': `1px solid #000000`,
      'font-weight': `bold`,
      'line-height': `22px`,
      'background-color': '#dedede'
  };
  const defaultMessageBodyCSS = {};
  const defaultButtonCSS = {
      'background-color': defaultThemeColor1,
      'border-radius': '6px',
      'min-width': '25px',
      'padding': '6px 8px'
  };

  const menuBoxCSS = {
    'position': 'fixed',
    'top': '0px',
    'left': '0px',
    'height': '30px',
    'color': '#000', 
    'border': '1px solid #000',
    'background-color': '#FFEBD6',
    'box-shadow': '0 0 2px #000',
    'display': 'block',
    'text-align': 'left',
    'font-size': '13px',
    'padding': '2px',
    'margin-bottom': '2px',
    'font-family': 'sans-serif',
    'white-space': 'pre-wrap'
  }

  const menuButtonCSS = {
    'background-color': '#521E10',
    'border': '1px',
    'color': 'white',
    'text-align': 'center',
    'display': 'inline-block',
    'font-size': '14px',
    'margin': '2px 1px',
    'cursor': 'pointer',
    'padding': '3px 6px',
    'border-radius': '4px'
  };

  const menuButtonCSS_Selected = {
    'background-color': '#521E10',
    'border': '1px',
    'color': 'black',
    'text-align': 'center',
    'display': 'inline-block',
    'font-size': '14px',
    'margin': '2px 1px',
    'cursor': 'pointer',
    'padding': '3px 6px',
    'border-radius': '4px'
  };

  const shadoweddivCSS = {
      'margin': '0px 16px 16px 0px',
      'padding': '5px 5px',
      'box-shadow': '5px 8px 8px #888888'
  };
  const boundingdivCSS = {
      'width' : '100%',
      'border' : 'dashed 0px'
  };
  const imgDivContainer = {
    'width': '50%', // Make the image width responsive to the container's width
    'height': 'auto', // Maintain the image's aspect ratio
    'overflow': 'hidden' // Hide any part of the image that goes beyond the container boundaries
  };
  const img1CSS = {
    'border' : '1px solid black',
  }

  const divC1 = {
    'flex' : '0 0 25%',
    'max-width' : '25%'
  }

  const divC2 = {
    'flex' : '0 0 75%',
    'max-width' : '75%'
  }

  const divC3 = {
    'display' : 'flex',
    'flex-direction' : 'row',
    'align-items' : 'center'
  }

  const divScrollBoxCSS = {
    'height' : '80vh',
    'overflow-y' : 'scroll',
    'border' : '1px solid black',
    'padding' : '5px',
    'display' : 'block'    
  }
  const combineCSS = (origCSS = {}, ...assignCSS) => {
      return Object.assign({}, origCSS, assignCSS.reduce((m, v) => {
          return Object.assign(m, v || {});
      }), {});
  };
  const confirmReadability = (origCSS = {}) => {
      let outputCSS = Object.assign({}, origCSS);
      if (outputCSS['background-color']) outputCSS['background-color'] = validateHexColor(outputCSS['background-color'] || "#dedede");
      if (outputCSS['color'] || outputCSS['background-color']) outputCSS['color'] = getTextColor(outputCSS['background-color'] || "#dedede");
      return outputCSS;
  };
  const assembleCSS = (css) => {
      return `"${Object.keys(css).map((key) => { return `${key}:${css[key]};` }).join('')}"`;
  };
  // HTML =======================================
  const html = {
      div: (content, CSS) => `<div style=${assembleCSS(combineCSS(defaultdivCSS, (CSS || {})))}>${content}</div>`,
      h1: (content, CSS) => `<h1 style=${assembleCSS(combineCSS(defaulth1CSS, (CSS || {})))}>${content}</h1>`,
      h2: (content, CSS) => `<h2 style=${assembleCSS(combineCSS(defaulth2CSS, (CSS || {})))}>${content}</h2>`,
      h3: (content, CSS) => `<h3 style=${assembleCSS(combineCSS(defaulth3CSS, (CSS || {})))}>${content}</h3>`,
      h4: (content, CSS) => `<h4 style=${assembleCSS(combineCSS(defaulth4CSS, (CSS || {})))}>${content}</h4>`,
      h5: (content, CSS) => `<h5 style=${assembleCSS(combineCSS(defaulth5CSS, (CSS || {})))}>${content}</h5>`,
      p: (content, CSS) => `<p style=${assembleCSS(combineCSS(defaultpCSS, (CSS || {})))}>${content}</p>`,
      table: (content, CSS) => `<table style=${assembleCSS(combineCSS(defaulttableCSS, (CSS || {})))}>${content}</table>`,
      th: (content, CSS) => `<th style=${assembleCSS(combineCSS(defaultthCSS, (CSS || {})))}>${content}</th>`,
      tr: (content, CSS) => `<tr style=${assembleCSS(combineCSS(defaulttrCSS, (CSS || {})))}>${content}</tr>`,
      td: (content, CSS) => `<td style=${assembleCSS(combineCSS(defaulttdCSS, (CSS || {})))}>${content}</td>`,
      td2: (content, CSS) => `<td colspan="2" style=${assembleCSS(combineCSS(defaulttdCSS, (CSS || {})))}>${content}</td>`,
      code: (content, CSS) => `<code style=${assembleCSS(combineCSS(defaultcodeCSS, (CSS || {})))}>${content}</code>`,
      a: (content, CSS, link) => `<a href="${link}" style=${assembleCSS(combineCSS(defaultaCSS, (CSS || {})))}>${content}</a>`,
      img:(content, CSS, link, title, width, height) => `<img src="${link}" style=${assembleCSS(combineCSS(defaultimgCSS, (CSS || {})))} alt="${content} width="${width}" height="${height}" title="${title}">`
  }
  // HTML Escaping function
  const HE = (() => {
      const esRE = (s) => s.replace(/(\\|\/|\[|\]|\(|\)|\{|\}|\?|\+|\*|\||\.|\^|\$)/g, '\\$1');
      const e = (s) => `&${s};`;
      const entities = {
          '<': e('lt'),
          '>': e('gt'),
          "'": e('#39'),
          '@': e('#64'),
          '{': e('#123'),
          '|': e('#124'),
          '}': e('#125'),
          '[': e('#91'),
          ']': e('#93'),
          '"': e('quot')
      };
      const re = new RegExp(`(${Object.keys(entities).map(esRE).join('|')})`, 'g');
      return (s) => s.replace(re, (c) => (entities[c] || c));
  })();
  // MESSAGING ==================================
  const btnAPI = ({ api: api = "", label: btnlabel = "Run API", css: css = defaultButtonCSS } = {}) => {
      let btnCSS = confirmReadability(css);
      //r20css = {};
      //if(['t', 'true', 'y', 'yes', true].includes(r20style)) Object.assign(r20css, {padding: '5px', display: 'inline-block', border: '1px solid white;'});
      return html.a(btnlabel, btnCSS, HE(api));
  };
  const msgbox = ({
      msg: msg = "message",
      title: title = "CampaignHealth Output",
      btn: btn = "buttons",
      sendas: sendas = "API",
      whisperto: whisperto = "",
      headercss: headercss = {},
      bodycss: bodycss = {}
  }) => {
      let hdrCSS = confirmReadability(combineCSS(defaultMessageHeaderCSS, headercss));
      let bodyCSS = confirmReadability(combineCSS(defaultMessageBodyCSS, bodycss));

      let hdr = html.tr(html.td(title, {}), hdrCSS);
      let body = html.tr(html.td(msg, {}), bodyCSS);
      let buttons = btn !== "buttons" ? html.tr(html.td(btn, { 'text-align': `right`, 'margin': `4px 4px 8px`, 'padding':'8px' }), {}) : "";

      let output = html.div(html.div(html.table(`${hdr}${body}${buttons}`, {}), shadoweddivCSS), boundingdivCSS);
      if (whisperto) output = `/w "${whisperto}" ${output}`;
      sendChat(sendas, output);
  };

  function getGMPlayerID() {
      const gmPlayers = findObjs({ _type: 'player' }).filter((player) => {
        return playerIsGM(player.get('_id'));
      });
      if (gmPlayers.length > 0) {
        return gmPlayers[0].get('_id');
      } else {
        log('No GM found');
        return null;
      }
  }
  function getAllPlayerIDsArray() {
      const players = findObjs({ _type: 'player' });
      const playerIds = players.map((player) => player.get('_id'));
      return playerIds;
  }
  function addTooltip(tt, item) {
    let newItem = `<div style="display:inline;" title="${tt}">${item}</div>`
    return newItem;
  }
  function makeButton(name, link, minwidth) {
    //const buttonStyle = `background-color: #cccccc25; color: #521E10 !important; font-weight:bold; border-radius: 0px; padding: 0px; margin: 1px 1px 1px 0px; display: inline-block`;  
    const buttonStyle = `background-color: #00000000; color: #521E10 !important; font-weight:bold; border-radius: 0px; padding: 0px; margin: 1px 1px 1px 0px; display: inline-block`;      
    if (minwidth === "f") {
      minwidth = "100%"
    } else {
      if (!minwidth) {
        minwidth = 'NONE'
      }
      minwidth = minwidth + "px"
    }
    if (minwidth == 'NONE'){
      minwidth = '';
    } else {
      minwidth =`width:${minwidth}`
    }
    if (!name) {
      name = "untitled"
    }
    if (link) {
      return `<a style = '${buttonStyle}; ${minwidth} !important' href='${link}'>${name}</a>`;
    } else {
      return `<div style = '${buttonStyle}; ${minwidth}; display:inline-block !important'>${name}</div>`;
    }
  }

  function pingToken(tokenId, mode) {
    //when Mode is:
    //  mode=0; Private ping for GM Only
    //  mode=1; Public ping for all
    const gmId = getGMPlayerID();
    const token = getObj('graphic', tokenId);
    if (token) {
      const left = token.get('left');
      const top = token.get('top');
      const pageId = token.get('_pageid');

      // Private or Public ping?
      if (mode=0){
        // Private
        sendPing(left, top, pageId, gmId, true, gmId);
      } else {
        // Public
        const playerIds = getAllPlayerIDsArray();
        sendPing(left, top, pageId, gmId, true, playerIds);
      }
    }
  }

  function moveToken(tId, posLeft, posTop){
    let t = getObj("graphic", tId);
    if (t) {
      if (posLeft !== undefined && posLeft !== null && posLeft !== "" && !isNaN(posLeft)){posLeft = 0}
      if (posTop !== undefined && posTop !== null && posTop !== "" && !isNaN(posTop)){posTop = 0}
      t.set("left", posLeft);
      t.set("top", posTop)
    }
  }
  function deleteObject(objType, objId){
    let obj = getObj(objType, objId);
    if (obj) {
      obj.remove()
    }
  }

  function getObjectValue(objType, objId, fieldName) {

    try {
      let obj = getObj(objType, objId);
      let val = obj.get(fieldName);
      return val
    } catch (err) {
      myDebug(`getObjectValue: ${err.message} for ObjType: ${objType} and ObjId: ${objId} and FieldName: ${fieldName}`);
      return ('Error')
    }

  }

  function myDebug(msg){
    if (gDebug) {
      sendChat('Debug','/w gm '+msg);
    }
  }

  function buildDBHealthCheckReport(){
      //  * DB Checker
      //    * Check for orphaned objects (characters/, attributes, ) (done)
      //      * graphic.pageid(done), graphic.cardid, graphic.represents, graphic.controlledby, graphic.bar#_link(done)
      //      * attribute.characterid(done)
      //      * ability.characterid(done)
      //      * handout.inplayersjournal(done), handout.controlledby(done)
      //      * Build an array of ids for each object type, then do a simple search of the id 
  
      //    * Check for duplicate objects (Characters(done), Attributes, Handouts(done), Page)
      //    * Check for Tokens off screen (done)
      //    * Check for objects with no Name (Pages, Handouts, Characters)
      //    * -- in token and character ids
      //    * Provide options to correct the issue 
  
      let output = ''
      let objs = []; //general use object array
      let charIds = []; // array of character ids
      let pageIds = []; // array of page ids
      let playerIds = []; // array of player ids
      let attIds = []
      let n = 0;
      let cnt = 0;
      let prevName = '';
      let pageName = '';
      let aryIPJ = []; // array of characterids
      let aryCB = [];
      
      output = html.h2(`Campaign Database Health Report`);
    
      // Build the base list of master table ids (Player, Character, Pages)
      const players = findObjs({ type: "player" });
      players.forEach(player => {
        playerIds.push(player.get("_id"))
      });
  
      const characters = findObjs({ type: "character" }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
      characters.forEach(character => {
        charIds.push(character.get("_id"))
      });
  
      const pages = findObjs({ type: "page" });
      pages.forEach(page => {
        pageIds.push(page.get("_id"))
      });
      
      const attributes = findObjs({ type: "attribute" });
      attributes.forEach(attribute => {
        attIds.push(attribute.get("_id"))
      });
  
      // Handouts: Do any reference players no longer in the game?
      output +=html.h3('Reviewing Handouts')
      objs = findObjs({ type: "handout" });
      cnt = 0;
      objs.forEach(handout => {
        aryIPJ = [];
        aryCB = [];
        if (handout.get("inplayerjournals")) {
          aryIPJ = handout.get("inplayerjournals").split(',');
        }
        if (handout.get("controlledby")){
          aryCB = handout.get("controlledby").split(',');
        }
        if (aryIPJ.length > 0) {
          if (aryIPJ[0] != 'all') {
            let diff = aryIPJ.filter(x => !playerIds.includes(x));
            if (diff.length > 0) {
              output += `Handout ${handout.get("name")} (inplayerjournals) references Players that no longer exist.<br>`
            }
          }
        }
        if (aryCB.length > 0) {
          if (aryCB[0] != 'all') {
            let diff = aryCB.filter(x => !playerIds.includes(x));
            if (diff.length > 0) {
              output += `Handout ${handout.get("name")} (controlledby) references Players that no longer exist.<br>`
            }
          }
        }
      });
  
      prevName = '';
      cnt = 0;
      objs.forEach(handout => {
        
        if (prevName === handout.get("name")){
          cnt++;
        } else {
          if (cnt > 0) {
            output += `${cnt} duplicate(s) Handouts found: <b>${prevName}</b><br>`
          }
          cnt = 0;
          prevName = handout.get("name");
        }
      });
      if (cnt > 0) { // Last handout in list
        output += `${cnt} duplicate(s) Handouts found: <b>${prevName}</b><br>`
      }
  
      // Characters: Look for duplicates
      output +=html.h3('Reviewing Characters')
      prevName = '';
      characters.forEach(char => {
        
        if (prevName === char.get("name")){
          cnt++;
        } else {
          if (cnt > 0) {
            output += `${cnt} duplicate(s) Character found: <b>${prevName}</b><br>`
          }
          cnt = 0;
          prevName = char.get("name");
        }
      });
      if (cnt > 0) { // Test last Character in list
        output += `${cnt} duplicate(s) Character found for character: <b>${prevName}</b><br>`
      }
  
      const doubleDashCharacters = characters.filter(char => char.id.includes('--'));
      doubleDashCharacters.forEach(char => {
        output += `Characters with a Double Dash "--" in their ID found in Character <b>${char.get('name')}</b> with id: <i>(${char.id})</i><br>`;
      });
  
      // Attributes: Look for attributes orphaned from a character
      output +=html.h3('Reviewing Attributes')
      objs = findObjs({ type: "attribute" });
      cnt = 0;
      objs.forEach(att => {
        n = charIds.indexOf(att.get("_characterid"))
        if (n < 0){
          output += `Orphaned Attrribute:<b>${att.get("name")}</b><br>`
          cnt++;
        }
      });
      //output += `<b>${cnt} orphanned attributes found.</b><br><br>`
  
      // Abilities: Look for abilities orphaned from a character
      output +=html.h3('Reviewing Abilities')
      objs = findObjs({ type: "ability" });
      cnt = 0;
      objs.forEach(ab => {
        n = charIds.indexOf(ab.get("_characterid"))
        if (n < 0){
          output += `Orphaned Ability: <b>${ab.get("name")}</b><br>`
          cnt++;
        }
      });
      //output += `<b>${cnt} orphanned abilities found.</b><br><br>`
  
      output +=html.h3('Reviewing Tokens')
      objs = findObjs({ type: "graphic", _subtype: "token"});
      cnt = 0;
      objs.forEach(token => {
        n = pageIds.indexOf(token.get("_pageid"))
        if (n < 0){
          output += `Token <b>${token.get("name")}</b> orphoned from Pages.<br>`
          cnt++;
        }
      });
      //output += `<b>${cnt} orphanned tokens from pages found.</b><br><br>`
  
      objs.forEach(token => {
        if (token.get("represents")){
          n = charIds.indexOf(token.get("represents"))
          if (n < 0){
            let btnMoveTkn = addTooltip("Move token to Top Left cell (0,0)", makeButton('Move', `!campaignhealth --FixTokenPosition ${token.get("_id")}`)) 
            let btnDeleteTkn = addTooltip("Delete Token", makeButton('Delete', `!campaignhealth --DeleteToken ${token.get("_id")}`)) 
            output += `Orphaned Tokens from character sheets: ${addTooltip("Ping Me", makeButton(token.get("name"), `!campaignhealth --PingToken ${token.get("_id")}`))} on page <b><i>${getObjectValue("page", token.get("_pageid"), "name")}</i></b> (${btnMoveTkn}) (${btnDeleteTkn})<br>`
            cnt++;
          }
        }
      });
      //output += `<b>${cnt} orphanned token from characters found.</b><br><br>`
  
      cnt=0;
      objs.forEach(token => {
        if (token.get("bar1_link")){
          n = attIds.indexOf(token.get("bar1_link"))
          if (n < 0){
            pageName = getObjectValue("page", token.get("_pageid"), "name")
            if (pageName == ''){pageName = 'Untitled'}
            output += `Orphaned Tokens bar1_link from attributes: ${addTooltip("Ping Me", makeButton(token.get("name"), `!campaignhealth --PingToken ${token.get("_id")}`))} on page <b><i>${pageName}</b></i><br>`
            cnt++;
          }
        }
      });
      //output += `<b>${cnt} orphanned token bar1_links from attributes found.</b><br><br>`
  
      cnt=0;
      objs.forEach(token => {
        if (token.get("bar2_link")){
          n = attIds.indexOf(token.get("bar2_link"))
          if (n < 0){
            pageName = getObjectValue("page", token.get("_pageid"), "name")
            if (pageName == ''){pageName = 'Untitled'}
            output += `Orphaned Tokens bar2_link from attributes: ${addTooltip("Ping Me", makeButton(token.get("name"), `!campaignhealth --PingToken ${token.get("_id")}`))} on page <b><i>${pageName}</b></i><br>`
            cnt++;
          }
        }
      });
      //output += `<b>${cnt} orphanned token bar2_links from attributes found.</b><br><br>`
  
      cnt=0;
      objs.forEach(token => {
        if (token.get("bar3_link")){
          n = attIds.indexOf(token.get("bar3_link"))
          if (n < 0){
            pageName = getObjectValue("page", token.get("_pageid"), "name")
            if (pageName == ''){pageName = 'Untitled'}
            output += `Orphaned Tokens bar3_link from attributes: ${addTooltip("Ping Me", makeButton(token.get("name"), `!campaignhealth --PingToken ${token.get("_id")}`))} on page <b><i>${pageName}</b></i><br>`
            cnt++;
          }
        }
      });
      //output += `<b>${cnt} orphanned token bar3_links from attributes found.</b><br><br>`
  
      objs.forEach(token => {
  
        let pageWidth = getObjectValue("page", token.get("_pageid"), "width") * 70.0
        let pageHeight = getObjectValue("page", token.get("_pageid"), "height") * 70.0
  
        if (token.get("left") > pageWidth || token.get("top") > pageHeight){
          let btnMoveTkn = addTooltip("Move token to Top Left cell (0,0)", makeButton('Move', `!campaignhealth --FixTokenPosition ${token.get("_id")}`)) 
          let btnDeleteTkn = addTooltip("Delete Token", makeButton('Delete', `!campaignhealth --DeleteToken ${token.get("_id")}`)) 
          output += `Token ${addTooltip("Ping Me", makeButton(token.get("name"), `!campaignhealth --PingToken ${token.get("_id")}`))} (${token.get("left")}, ${token.get("top")}) is off the viewing are of page ${getObjectValue("page", token.get("_pageid"), "name")} (${pageWidth}, ${pageHeight}) (${btnMoveTkn}) (${btnDeleteTkn})<br>`
          cnt++;
        }
      });

      const doubleDashTokens = objs.filter(token => token.id.includes('--'));
      let tName = '';

      doubleDashTokens.forEach(token => {
        if (token.get('name') =='' || token.get('name').length == 0) {
          tName = 'BLANK'
        } else {
          tName = token.get('name');
        }
        pageName = getObjectValue("page", token.get("_pageid"), "name")
        if (pageName == ''){pageName = 'Untitled'}

        output += `Tokens with a Double Dash "--"  in their ID found in Token ${addTooltip("Ping Me", makeButton(tName, `!campaignhealth --PingToken ${token.get("_id")}`))}  with id: <i>(${token.get('_id')})</i> on page <i>${pageName}</i><br>`;
      });

  
      output = openReport + output + closeReport;
    
      // Check if a "Campaign Health" handout exists, or create one
      let dbReport = findObjs({ type: "handout", name: "Campaign Health Report"})[0];
      if (!dbReport) {
        dbReport = createObj("handout", { name: "Campaign Health Report"});
      }
  
      // Update the "Player Access" handout content
      dbReport.set("notes", output);
    }

    function moveToken(tId, posLeft, posTop){
      let t = getObj("graphic", tId);
      if (t) {
        if (posLeft !== undefined && posLeft !== null && posLeft !== "" && !isNaN(posLeft)){posLeft = 0}
        if (posTop !== undefined && posTop !== null && posTop !== "" && !isNaN(posTop)){posTop = 0}
        t.set("left", posLeft);
        t.set("top", posTop)
      }
    }
  
    function deleteObject(objType, objId){
      let obj = getObj(objType, objId);
      if (obj) {
        obj.remove()
      }
    }
  
  function buildPlayerAccessReport() {
    let playerAry = []

    let btnFct  = '';
    let access = '';
    let rowAccessCnt = 0;
    let tblRow = '';
    let tblDebug = ''

    // Initialize an empty string to store the information
    let output = '';
    let tblAccess = ''
    let playerCount = 0;

    let cb=''
    let ipj=''

    // Get all players
    const players = findObjs({ type: "player" }).sort((a, b) => (a.get("_displayname") > b.get("_displayname") ? 1 : -1));
  

    // Add the player's name to the content string
    output = html.h2(`Player Access Report`);
    output += html.p(`<i>&nbsp;&nbsp;${emojiNote}=In Player's Journal     ${emojiAccess}=Controlled By</i>`);
    tblAccess = html.th('Object Name')
    tblDebug = html.th('PlayerIds')

    // Iterate through all players
    players.forEach(player => {
      // Get the player's name
      let playerName = player.get("displayname");
      let playerId = player.get("_id");
      if (!playerIsGM(playerId)) {
        playerAry.push({name: playerName, id: playerId});
        tblAccess += html.th(playerName)
        tblDebug += html.td(playerId, {'font-size' : '9px'})
        playerCount++;
      }
    })

    access = '';
    tblAccess = html.tr(tblAccess);
    // tblAccess += html.tr(tblDebug);
    tblAccess += html.tr(html.td("<h3>Handouts</h3>"))
    let handouts = findObjs({ type: "handout" }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
    handouts.forEach(handout => {
      access = '';
      btnFct = addTooltip("Open Handout", makeButton(emojiDocument, `https://journal.roll20.net/handout/${handout.get('_id')}`));
      let tblRow = html.td("&nbsp;&nbsp;" + btnFct + handout.get("name"))  // Add link to handout later
      playerAry.forEach(player => {
        ipj = handout.get("inplayerjournals");
        if(ipj.includes(player.id) || ipj === "all"){
          access += emojiNote
          rowAccessCnt++;
        }
        
        cb = handout.get("controlledby");
        if (cb.includes(player.id) || cb === "all") {
          access += emojiAccess
          rowAccessCnt++;
        }
        tblRow += html.td(access, {'text-align': 'center'})
        access = '';
      });
      if (rowAccessCnt > 0) {
        tblAccess += html.tr(tblRow);
      }
      rowAccessCnt = 0;
    })

    tblRow = html.td("<h3>Charactes</h3>")
    playerAry.forEach(player => {
      // Get the player's name
        tblRow += html.td(`<b>${player.name} </b>`)  // Add link to handout later
    });
    tblAccess += html.tr(tblRow);

    let characters = findObjs({ type: "character" }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
    characters.forEach(character => {
      access = '';

      btnFct = addTooltip("Open Character Sheet", makeButton(emojiDocument, `https://journal.roll20.net/character/${character.get('_id')}`));
      tblRow = html.td("&nbsp;&nbsp;" + btnFct + character.get("name") )  // Add link to handout later
      playerAry.forEach(player => {
        ipj = character.get("inplayerjournals");
        if(ipj.includes(player.id) || ipj === "all"){
          access += emojiNote
          rowAccessCnt++;
        }
        
        cb = character.get("controlledby");
        if (cb.includes(player.id) || cb === "all") {
          access += emojiAccess
          rowAccessCnt++;
        }
        tblRow += html.td(access, {'text-align': 'center'})
        access = '';
      });
      if (rowAccessCnt > 0) {
        tblAccess += html.tr(tblRow);
      }
      rowAccessCnt = 0;
    })

    tblRow = html.td("<h3>Tokens</h3>")
    playerAry.forEach(player => {
      // Get the player's name
        tblRow += html.td(`<b>${player.name} </b>`)  // Add link to handout later
    });
    tblAccess += html.tr(tblRow);

    let pages = findObjs({ type: "page" }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
    pages.forEach(page => {
      let graphics = findObjs({type: "graphic", _pageid: page.get("_id")}).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));
      graphics.forEach(graphic => {
        tblRow = html.td("&nbsp;&nbsp;" + page.get("name") + ": " + graphic.get("name"))  // Add link to handout later

        if (graphic.get("represents")!== undefined && graphic.get("represents").length > 0){
          character = getObj("character", graphic.get("represents"));
          if (character) {
            cb = character.get("controlledby");
          } else {
            cb = 'N/A'
          }
        } else {
          cb = graphic.get("controlledby");
        }

        playerAry.forEach(player => {
          if (cb.includes(player.id) || cb === "all") {
            access += emojiAccess
            rowAccessCnt++;
          }
          tblRow += html.td(access, {'text-align': 'center'})
          access = '';
        });
        if (rowAccessCnt > 0) {
          tblAccess += html.tr(tblRow);
        }
        rowAccessCnt = 0;        
      });
    });
    tblAccess = html.table(tblAccess);

    output += tblAccess + html.p('<i><b>Note: </b>Objects not listed in the table above are only "controlled by" and "in the journal" of the GM.</i>');
    output = openReport + output + closeReport;
  
    // Check if a "Player Access" handout exists, or create one
    let playerAccessHandout = findObjs({ type: "handout", name: "Player Access Report" })[0];
    if (!playerAccessHandout) {
      playerAccessHandout = createObj("handout", { name: "Player Access Report" });
    }
  
    // Update the "Player Access" handout content
    playerAccessHandout.set("notes", output);
  
  }    

  function mySendChat(whisper, title, message, links){
    let chatTxt = '';

    if(whisper == true){
      chatTxt = `/w gm `
    }
    chatTxt += `${openChat}<table style="${tblChatStyle}"><tr style="${trhChatStyle}"><td style="${tdChatStyle}">${title}</td></tr>`
    chatTxt += `<tr><td style="${tdChatStyle}">${message}</td></tr>`
    if (links){
      chatTxt += `<tr><td style="${tdChatStyle}">${links}</td></tr>`
    }
    chatTxt += `</table>${closeChat}`
    sendChat('Campaign Health', chatTxt);
  }

  function deleteReports(){
    let ho = findObjs({ type: "handout", name: "Player Access Report" })[0];
    if (ho) {
      ho.remove();
    }

    ho = findObjs({ type: "handout", name: "Campaign Health Report" })[0];
    if (ho) {
      ho.remove();
    }
  }

  function getHandout(handoutName) {
    let handout = findObjs({
        _type: 'handout',
        name: handoutName
    })[0];

    if (!handout) {
        handout = createObj('handout', {
            name: handoutName,
            archived: false
        });
    }
    return handout;
  }


  // ****************************************************************
  // End of embedded Functions 
  // ****************************************************************

  const gDebug = false;

  const emojiAccess = '\u{2705}' //✅
  const emojiPing = '\u{1F3AF}' // 🎯
  const emojiDocument = '\u{1F4D1}' // 📑
  const emojiNote = '\u{1F4D3}' // 📓

  const htmlCR = '&#13;'
  const htmlRBrace = '&#125;'
  const htmlLBrace = '&#123;'
  const htmlPct = '&#37;'
  const htmlAt = '&#64;'
  
  const openReport = "<div style='color: #000; border: 1px solid #000; background-color: #EFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 5px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
  const closeReport = '</div>';

  const openChat = `<div style="border-radius: 10px ; border: none ; background-color: ; overflow: hidden ; width: 100%"><div style = "border-radius: 10px ; border: 2px solid #000000 ; background-color:  #00000000; overflow: hidden ; margin: 0px 16px 16px 0px ; box-shadow: 5px 8px 8px #888888">`
  const closeChat= `<\div><\div>`;
  const tblChatStyle = `width: 100% ; margin: 0 auto ; border-collapse: collapse ; font-size: 12px;`
  const trhChatStyle = `border-bottom: 1px solid #000000 ; font-weight: bold ; line-height: 22px ; background-color: #521e10 ; color: #ffffff;`
  const tdChatStyle = `padding: 4px ; min-width: 10px;`
  const openBox = "<div style='margin-top: 40px;color: #000; border: 1px solid #000; background-color: #FFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 2px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
  const openMenuBox = "<div style='position:fixed; top:0px left:0px; height:30px; color: #000; border: 1px solid #000; background-color: #FFEBD6; box-shadow: 0 0 2px #000; display: block; text-align: left; font-size: 11px; padding: 2px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";    
  const closeBox = '</div>';
  const openScrollBox = `<div style='height:80vh; overflow-y: scroll; border: 1px solid black; padding 5px; display: block;'>`
  const closeScrollBox = '</div>';

  let chatMsg = ''

  let args = msg_content.split(/\s--/);
  let commands = [];

  if (args.length > 1) {
    commands = args[1].match(/(?:[^\s"']+|"[^"]*")+/g);
    commands = commands.map(item => item.replace(/^"|"+$/g, ''));
  } else {
    buildDBHealthCheckReport();
    buildPlayerAccessReport();
    // Notify the GM
    chatMsg = `The Campaign Health and Player Access report handouts have been updated.  Click the links below to view:`;      
    chatMsg += `<br><br>&nbsp;<b>[Campaign Health Report](https://journal.roll20.net/handout/${getHandout('Campaign Health Report').get('_id')})</b>`;      
    chatMsg += `<br><br>&nbsp;<b>[Player Access Report](https://journal.roll20.net/handout/${getHandout('Player Access Report').get('_id')})</b>`;
    chatMsg += `<br><br>Click ${makeButton('DELETE', '!campaignhealth --deletereports')} to <b>remove</b> the Campaign Health and Player Access reports`
    chatMsg += `<br><br>Click ${makeButton('Refresh', '!campaignhealth')} to <b>rebuild</b> the contents of the Campaign Health and Player Access reports`

    // sendChat("DM Dashboard", chatMsg);
    mySendChat(true, "Campaign Health", chatMsg)
    return;
  }

  commands[0] = commands[0].toUpperCase();
  myDebug(`MsgHandler: commands[0]: ${commands[0]}`)  

  let masterCmd = args[0].toUpperCase()

  if (masterCmd =='!CAMPAIGNHEALTH') {
    switch (commands[0]) {

      case 'PINGTOKEN':
        pingToken(commands[1],0);
        break
      case 'FIXTOKENPOSITION':
        chatMsg = `Token <b>${getObjectValue('graphic', commands[1], 'name')}</b> has been moved to the top left corner of the page <i>${getObjectValue('page', getObjectValue('graphic', commands[1], '_pageid'), 'name')}</i>`
        mySendChat(true, "Campaign Health", chatMsg)        
        moveToken(commands[1], 0,0);
        break;

      case 'DELETETOKEN':
        chatMsg = `Token <b>${getObjectValue('graphic', commands[1], 'name')}</b> has been deleted from page <i>${getObjectValue('page', getObjectValue('graphic', commands[1], '_pageid'), 'name')}</i> of your campaign`
        mySendChat(true, "Campaign Health", chatMsg)        
        deleteObject('graphic', commands[1]);
        break;
      case 'DELETEREPORTS':
        deleteReports();
        chatMsg = `The <b>Campaign Health</b> and <b>Player Access</b> report handouts have been deleted`;
        chatMsg += `<br><br>Click ${makeButton('Refresh', '!campaignhealth')} to <b>rebuild</b> the contents of the Campaign Health and Player Access reports`        
        mySendChat(true, "Campaign Health", chatMsg)
        break;
      default:
        mySendChat(true, "Campaign Health", `Command ${commands[0]} not recognized.`)
        break;
      
    }
  } 
}
});
 
{try{throw new Error('');}catch(e){API_Meta.CampaignHealth.lineCount=(parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/,'$1'),10)-API_Meta.CampaignHealth.offset);}}