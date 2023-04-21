var API_Meta = API_Meta || {};
API_Meta.TOReporter = { offset: Number.MAX_SAFE_INTEGER, lineCount: -1 };
{
  try { throw new Error(''); } catch (e) { API_Meta.TOReporter.offset = (parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/, '$1'), 10) - (4)); }
}

/***********************************
*** Start of Handout DM Reporter ***  
***********************************/

// Future Enhancements
//  1. Add saving throw to the character sheet
//  2. replace some of my other gmnotes/notes update fields with the new asyn method

/******************************
***     Event Management    ***  
*******************************/
on('ready', () => {
  const version = '0.6.0';
  log('DM Turnorder Reporter' + version + ' is ready! --offset '+ API_Meta.TOReporter.offset);
  log(' To start using TOR, in the chat window enter `!tor`');

  API_Meta.TOReporter.version = version;

  log(`State: ${state.TOReporter} PrevTO: ${typeof state.TOReporter.PrevTO}` )
  if (typeof state.TOReporter.PrevTO === 'undefined') {
    log('PrevTO not defined');
  }

  // Check if the namespaced property exists, creating it if it doesn't
  if(!state.TOReporter) {
      state.TOReporter = {
          version: 5.0,
          DetailExpand: 1,
          LastDT1: 0,
          LastUTCDate: '',
          DM_Count: 0,
          DM_Avg: 0,
          DM_Secs: 0,
          PrevTO:[]
      };
  };
});

on('change:campaign:turnorder', async () => {
  log('HO Event: change:campaign:turnorder');

  debounced_torHandleMsg('!tor --TOReport')   
  // torHandleMsg('!tor --TOReport')
});

on('chat:message', async (msg_orig) => {
  let msg = _.clone(msg_orig);
  if (!/^!tor/.test(msg.content)) {
    return;
  }
  log('HO Event: chat:message');
  debounced_torHandleMsg(msg.content);
  // torHandleMsg(msg.content);
});

const debounced_torHandleMsg = _.debounce(torHandleMsg,500)

/******************************
*** Global Functions        ***  
*******************************/

function torHandleMsg(msg_content){

  function updateTurnOrderStartTime(){
    state.TOReporter.LastDT1 = getSystemTimeInSecs();
    state.TOReporter.LastUTCDate = GetSystemUTCDate();
  }

  function myGetAttrByName(character_id,
                           attribute_name,
                           attribute_default_current,
                           attribute_default_max,
                           value_type) {
      attribute_default_current = attribute_default_current || '';
      attribute_default_max = attribute_default_max || '';
      value_type = value_type || 'current';

      var attribute = findObjs({
          type: 'attribute',
          characterid: character_id,
          name: attribute_name
      }, {caseInsensitive: true})[0];
      if (!attribute) {
          attribute = createObj('attribute', {
              characterid: character_id,
              name: attribute_name,
              current: attribute_default_current,
              max: attribute_default_max
          });
      }

      if (value_type == 'max') {
          return attribute.get('max');
      } else {
          return attribute.get('current');
      }
  }

  function DidTOAdvance(){
    let cmd_advance = 0; //Boolean value where next_cmd = 1 means TO changed as a result of advancing the TO

    let prev_to = state.TOReporter.PrevTO;
    let curr_to = Campaign().get('turnorder');

    // log(`DidTOAdvance: Prev:${prev_to} Curr:${curr_to}`)
    state.TOReporter.PrevTO = curr_to;

    if (prev_to.length = 0) {
      return 0;
    }

    
    if (!prev_to || !curr_to){
      return 0;
    }

    if (!prev_to || !curr_to || prev_to == '' || curr_to == '') {
        log('TO Change: Exit-Empty TurnOrder');
        return 0;
    }

    // take the JSON string and convert it to an object
    prev_to_json = JSON.parse(prev_to);
    curr_to_json = JSON.parse(curr_to);
    log('TO Change: Test Lengths(curr vs. prev): ' + curr_to_json.length + ' / ' + prev_to_json.length);

    if (prev_to_json.length == 0 || curr_to_json.length == 0){
      return 0;  
    }
    
    if (prev_to_json.length == curr_to_json.length) {

      log('TO Change: Same Length');
      //Shift the prev_to and see if is now equivalent to the curr_to    
      prev_to_json.push(prev_to_json.shift());
      // Update any formulas
      if (prev_to_json[0].id == -1 && prev_to_json[0].formula && prev_to_json[0].formula !== '') {
              prev_to_json[0].pr = prev_to_json[0].pr + eval(prev_to_json[0].formula);
      }

      if (JSON.stringify(prev_to_json) == curr_to) {
        // If they are equivalent, then the only change was because the turn changed
        log('TO Change: TOs are equal: ');
        cmd_advance = 1;
        AddToTurnorderLog();
      } else {
        log('TO Change: TOs are not equal:');
        log('Prev_To:' + JSON.stringify(prev_to_json));
        log('Curr_To:' + curr_to);
      }
    }
    log('DidTOAdvance: ' + cmd_advance);
    return cmd_advance;  
  };

  function createNoteLog() {
    const noteLog = createObj('handout',{
      name: 'DM Turnorder Log'
    });
    noteLog.set('notes', '<h3>Turnorder Log</h3>');
    return noteLog;
  };

  function getNoteLog() {
    const noteLog = filterObjs(function(o){
      return ( 'handout' === o.get('type') && 'DM Turnorder Log' === o.get('name') && false === o.get('archived'));
    })[0];

    if(noteLog) {
      return noteLog;
    } 
    return createNoteLog();
  };

  function GetSystemUTCDate() {
    let d = new Date();
    let utcDate = `${d.getUTCMonth().toString().padStart(2,'0')}/${d.getUTCDate().toString().padStart(2,'0')}/${d.getUTCFullYear()} ${d.getUTCHours().toString().padStart(2,'0')}:${d.getUTCMinutes().toString().padStart(2,'0')}:${d.getUTCSeconds().toString().padStart(2,'0')}`;
    return utcDate;
  };


  function AddToTurnorderLog(){
  // Purpose:  Write a csv record to a handout called 
    let toChar = [];
    let toObj = [];
    let toTkn = [];
    let NewData = '';
    let repeatingSection = '';  //Prefix
    let repeatingName = '';      //Suffix 
    let repeatingValues = [];
    let cName = '';
    let cLevel = '';
    let cClass = '';
    let cRace = '';
    let cCasterLevel = '';
    let cAtkCnt = 0;
    let cTraitCnt = 0;
    let Ttype = '';
    let nl = [];

    log('********** AddTurnOrderLog ***************');
    let curr_to = Campaign().get("turnorder");
    if (curr_to === '') {
      return;
    }

    //Get the Turn Order object into a Json object
    toObj = JSON.parse(curr_to);

    //Get the Current System Time
    let currDate = GetSystemUTCDate();

    //Get Last System Time state.TOReporter.LastDT1
    let prevDate = state.TOReporter.LastUTCDate;

    log('TO:1');
    // What is this turnorder tied to?  A pc, npc, custom item or other?
    Ttype = getTokenType(toObj[toObj.length-1].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER
    //log('TO?:' + Ttype);

    switch (Ttype) {

      case 'CUSTOM':
        NewData = `CUSTOM,${prevDate},${currDate},${toObj[toObj.length-1].id},${toObj[toObj.length-1].pr},${toObj[toObj.length-1].custom},0,0,0,0,0,0,0`
        break;

      case 'CHAR':
      
        // TokenType, Start, End, 'Char', TurnOrderId, pr, CharacterName, 0, Level, Class, Race, Attacks_Count, Traits_Count, Spells_Count
        toTkn = getObj("graphic", toObj[toObj.length-1].id);
        toChar = getObj("character", toTkn.get('represents'));
        cName = toChar.get('name')
        cLevel = getAttrByName(toChar.get('_id'),'level','current');
        cClass = getAttrByName(toChar.get('_id'),'class_display','current');
        cRace = getAttrByName(toChar.get('_id'),'race_display','current');
        cCasterLevel = getAttrByName(toChar.get('_id'),'caster_level','current');
          
        repeatingSection = 'repeating_attack';  //Prefix
        repeatingName = 'atkname';              //Suffix 
        repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
          .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
        cAtkCnt = repeatingValues.length
          
        repeatingSection = 'repeating_traits';  //Prefix
        repeatingName = 'name';                 //Suffix 
        repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
          .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
        cTraitCnt = repeatingValues.length
        NewData = `CHAR,${prevDate},${currDate},"'${toObj[toObj.length-1].id}",${toObj[toObj.length-1].pr},"${cName}",0,"${cLevel}","${cClass}","${cRace}",${cAtkCnt},${cTraitCnt},${cCasterLevel}`
        break;

      case 'NPC':

          // TokenType, Start, End, 'NPC', TurnOrderId, pr, Name, 0, CR, Type, SubType, Attack_Count(A, RA, BA, LA), Traits_Count, Spells_Count
          toTkn = getObj("graphic", toObj[toObj.length-1].id);
          toChar = getObj("character", toTkn.get('represents'));
          cName = toChar.get('name')
          cLevel = getAttrByName(toChar.get('_id'),'npc_challenge','current');
          cClass = getAttrByName(toChar.get('_id'),'npc_type','current');
          cRace = ''
          cCasterLevel = getAttrByName(toChar.get('_id'),'caster_level','current');

          repeatingSection = 'repeating_npcaction';  //Prefix
          repeatingName = 'name'                    //Suffix 
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
          cAtkCnt = repeatingValues.length

          repeatingSection = 'repeating_npctrait';  //Prefix
          repeatingName = 'name'                    //Suffix 
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));
          cTraitCnt = repeatingValues.length

          //         NPC, Start,       End,       TurnOrderId,               pr,                         Name,    0,  Level,    Class,   N/A,      Attacks,   Traits,      CasterLvl
          NewData = `NPC,${prevDate},${currDate},"'${toObj[toObj.length-1].id}",${toObj[toObj.length-1].pr},"${cName}",0,"${cLevel}","${cClass}","${cRace}",${cAtkCnt},${cTraitCnt},${cCasterLevel}`
          break;

        case 'OTHER':
          //         OTHER, Start,       End,       TurnOrderId,               pr,                         Name,    0,  Level,    Class,   N/A,      Attacks,   Traits,      CasterLvl
          // Start, End, 'Other', TurnOrderId, pr, TokenName, 0,0,0,0,0,0,0,0,0,
          log('TO:OTHER');
          toTkn = getObj("graphic", toObj[toObj.length-1].id);
          cName = toTkn.get('name');
          NewData = `OTHER,${prevDate},${currDate},"'${toObj[toObj.length-1].id}",${toObj[toObj.length-1].pr},${cName},0,0,0,0,0,0,0`
          break;
    }

    // Write record to Handout "DM Turnorder Log"
    nl = getNoteLog();
    nl.get('notes', function(n){
      if(!_.isNull(n)){
        setTimeout(function(){
          let text=n+'<br>' + NewData;
          nl.set('notes',text);                        
        },0);
      }
    });
  };

  function tokenToggleVisibility(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }

    if (token.get('layer')=='objects'){
      token.set('layer', 'gmlayer');
    } else {
      token.set('layer', 'objects');
    }
  }

  function tokenAdjHP(tId, adjHP){
    //If there is a '+'' or '-' in front of HP, will adjust hp
    //  Otherwise, it will set it.
    log(`tokenAdjHP1: tId: ${tId} AdjHP:${adjHP}`)    
    
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }
    let hp = adjHP.trimStart().trimEnd();

    log(`tokenAdjHP2: tId: ${tId} AdjHP:${adjHP} hp:${hp}`)

    if (hp[0] == '+' || hp[0] == '-'){
      hp = Number(token.get('bar1_value')) + Number(hp)
      token.set('bar1_value', hp);
    } else {
      token.set('bar1_value', hp);
    }
  }

  function tokenSetTooltip(tId, textToAdd){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }
    log(`tokenSetTooltip: tId:${tId} Text:${textToAdd}`);
    token.set('tooltip', textToAdd);
  }
  
  function tokenClearTooltip(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }
    token.set('tooltip', '');
  }

  function tokenToggleTooltip(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }

    if (token.get('show_tooltip')){
      token.set('show_tooltip',false);
    } else {
      token.set('show_tooltip',true);
    }
  }

  function tokenToggleLock(tId){
    const token = getObj('graphic', tId);
    if (!token){
      return;
    }

    if (token.get('lockMovement')){
      token.set('lockMovement',false);
    } else {
      token.set('lockMovement',true);
    }
  }


  function addTextToCharGMNote(characterId, text){
    const char = getObj('character', characterId);
    if (!char) {
      sendChat('API', `/w gm Character with ID ${characterId} not found.`);
      return;
    }

    char.get('gmnotes', function(n){
      if(!_.isNull(n)){
        setTimeout(function(){
          let newText=n+'<br>' + text;
          char.set('gmnotes',newText);                        
        },0);
      }
    });
  }

  const addTextToTokenGMNote = async (tokenId, textToAdd) => {
    const token = getObj('graphic', tokenId);

    if (!token) {
      sendChat('API', `/w gm token with ID ${tokenId} not found.`);
      return;
    }

    const currentNotes = token.get('gmnotes');
    const updatedNotes = currentNotes === 'null' ? textToAdd : `${currentNotes}<br>${textToAdd}`;

    // Update the bio field asynchronously using a Promise
    return new Promise((resolve) => {
      token.set('gmnotes', updatedNotes, (err) => {
        if (err) {
          sendChat('API', `/w gm Error updating token notes: ${err.message}`);
          resolve(false);
        } else {
          sendChat('API', `/w gm Successfully added text to token notes for character ${token.get('name')}.`);
          resolve(true);
        }
      });
    });
    log(`AddTextToTokenGMNote: tokenId:${tokenId} text:${textToAdd} currText:${currentNotes} newText:${updatedNotes}`)
  };

  const addTextToGMNote = async (textToAdd) => {
    const ho = getHandout('DM Turnoder List');

    if (!ho) {
      sendChat('API', `/w gm handout "DM Turnoder List" not found.`);
      return;
    }

    ho.get('gmnotes', function(n){
      if(!_.isNull(n)){
        setTimeout(function(){
          let text=textToAdd + '<br>'+ n;
          ho.set('gmnotes',text);                        
        },0);
      }
    });

    sendChat('API', `/w gm Tried to add "${textToAdd}" to gmnotes `);

  };

  function getTokenType(toId) {
    // Given a TurnOrderId 
    //  Returns UTILITY, CUSTOM, PC, NPC, or OTHER

    let cObj = []; //Character
    let tObj = []; //Token

    if (toId == -1) {
      return 'CUSTOM';
    }

    tObj = getObj("graphic", toId);
    if (!tObj) {
      return 'OTHER';
    }

    if(tObj.get("represents") == ""){
      return 'OTHER';
    }

    cObj = getObj("character", tObj.get('represents'));
    if (!cObj){
      return 'OTHER';
    }

    // Ignore Tokens tied to DM Functionality
    if (myGetAttrByName(cObj.get('_id'),'Init_Ignore','current')==1){
      return 'UTILITY';
    }

    if (cObj.get('controlledby')=='' || getAttrByName(cObj.get('_id'),'npc','current')==1){
        return 'NPC';
    };

    // If we got all here - then this Turnorder Id is associated with a token
    return 'CHAR';
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
    } else {
      log('Token not found');
    }
  }

  function to_MoveNext() {
    // First - Make sure the turnorder is populated
    var turnOrder = Campaign().get('turnorder');
    if (turnOrder === '') {
        sendChat('AdvanceTurnOrder', 'Turn order is empty.');
        return;
    }
    // take the JSON string and convert it to an object
    turnOrder = JSON.parse(turnOrder);
    if (turnOrder.length === 0) {
      sendChat('AdvanceTurnOrder', 'Turn order is empty.');
      return;
    }

    // Move the current item to the end of the list
    var currentItem = turnOrder.shift();
    turnOrder.push(currentItem);

    // Update any countdown formulas
    if (turnOrder[0].id == -1 && turnOrder[0].formula && turnOrder[0].formula !== '') {
            var newValue = turnOrder[0].pr + eval(turnOrder[0].formula);
            turnOrder[0].pr = newValue;
    }

    // Update the campaign's turn order
    turnOrder = JSON.stringify(turnOrder);
    Campaign().set('turnorder', turnOrder);
  }

  function to_MovePrev() {
    // First - Make sure the turnorder is populated
    var turnOrder = Campaign().get('turnorder');
    if (turnOrder === '') {
        sendChat('PrevTurnOrder', 'Turn order is empty.');
        return;
    }

    // take the JSON string and convert it to an object
    turnOrder = JSON.parse(turnOrder);
    if (turnOrder.length === 0) {
      sendChat('PrevTurnOrder', 'Turn order is empty.');
      return;
    }
    last_turn = turnOrder.pop();        
    turnOrder.unshift(last_turn);
    Campaign().set('turnorder', JSON.stringify(turnOrder));
  }

  function to_AddCustom(itemName, position, formula) {
    const turnorder = JSON.parse(Campaign().get('turnorder') || '[]');
    let newItem = [];
    if (formula){
      newItem = {
        id: "-1",
        pr: position, // Set the desired initiative value (default: 0)
        formula: formula, 
        custom: itemName,
        _pageid: Campaign().get("playerpageid")
      };
    } else {
      newItem = {
        id: "-1",
        pr: position, // Set the desired initiative value (default: 0)
        custom: itemName,
        _pageid: Campaign().get("playerpageid")
      };    
    }

    turnorder.unshift(newItem);
    Campaign().set('turnorder', JSON.stringify(turnorder));
  }

  function to_Sort() {
    let turnOrder = JSON.parse(Campaign().get('turnorder'));
    if (!turnOrder || turnOrder.length === 0) {
        sendChat('SortTurnOrder', '/w gm Turn order is empty, unable to sort.');
        return;
    }
    turnOrder.sort(function(a, b) {
        if (a.pr < b.pr) return 1;
        if (a.pr > b.pr) return -1;
        return 0;
    });

    Campaign().set('turnorder', JSON.stringify(turnOrder));
    // sendChat('SortTurnOrder', '/w gm Turn order has been sorted by initiative.');
  }

  function to_SortWrapped() {
    let turnOrder = JSON.parse(Campaign().get('turnorder'));

    if (!turnOrder || turnOrder.length === 0) {
        sendChat('SortTurnOrder', '/w gm Turn order is empty, unable to sort.');
        return;
    }
    let anchorItem = turnOrder[0];

    // Sort the turnorder proper
    turnOrder.sort(function(a, b) {
        if (a.pr < b.pr) return 1;
        if (a.pr > b.pr) return -1;
        return 0;
    });

    // Rotate the turn order until it matches our Anchor item
    let x = 0; // safeguard to prevent an infinite loop
    while(anchorItem.pr != turnOrder[0].pr && anchorItem.id != turnOrder[0].id && x <= turnOrder.length){
      // Rotate Turn Order
      var currentItem = turnOrder.shift();
      turnOrder.push(currentItem);
      x = x + 1;
    }
    Campaign().set('turnorder', JSON.stringify(turnOrder));
  }

  function to_Clear() {
    Campaign().set('turnorder', '[]');
  }  

  function to_Remove(itemId){
    const turnOrder = JSON.parse(Campaign().get('turnorder'));
    if (!turnOrder || turnOrder.length === 0) {
      log(`to_Remove: Turn order is empty, unable to remove item.`);
      return;
    }
    const itemToRemove = turnOrder.findIndex(item => item.id === itemId);
    if (itemToRemove === -1) {
      log(`to_Remove: Item with ID "${itemId}" not found in the turn order.`);
      return;
    }
    turnOrder.splice(itemToRemove, 1);
    Campaign().set('turnorder', JSON.stringify(turnOrder));
    log(`to_Remove: Item with ID "${itemId}" has been removed from the turn order.`);
  }

  function to_RemoveCustom(ndx){
    const turnOrder = JSON.parse(Campaign().get('turnorder'));
    if (!turnOrder || turnOrder.length === 0) {
      log(`to_RemoveCustom: Turn order is empty, unable to remove item.`);
      return;
    }
    if (ndx < 0 || ndx > (turnOrder.length-1)) {
      log(`to_RemoveCustom: Item with Index Position of "${ndx}" is outside the range of turn order.`);
      return;
    }
    turnOrder.splice(ndx, 1);
    Campaign().set('turnorder', JSON.stringify(turnOrder));
    log(`to_RemoveCustom: Item with Index Position of "${ndx}" has been removed from the turn order.`);
  }

  const getRepeatingSectionAttrs = function (charid, prefix) {
    // Input
    //  charid: character id
    //  prefix: repeating section name, e.g. 'repeating_weapons'
    // Output
    //  repRowIds: array containing all repeating section IDs for the given prefix, ordered in the same way that the rows appear on the sheet
    //  repeatingAttrs: object containing all repeating attributes that exist for this section, indexed by their name
    const repeatingAttrs = {},
        regExp = new RegExp(`^${prefix}_(-[-A-Za-z0-9]+?|\\d+)_`);
    let repOrder;
    // Get attributes
    findObjs({
        _type: 'attribute',
        _characterid: charid
    }).forEach(o => {
      const attrName = o.get('name');
      // log('getRepeatingSectionAttrs 1: ' + attrName);
      if (attrName.search(regExp) === 0) repeatingAttrs[attrName] = o;
      else if (attrName === `_reporder_${prefix}`) repOrder = o.get('current').split(',');
    });
    if (!repOrder) repOrder = [];
    // Get list of repeating row ids by prefix from repeatingAttrs
    const unorderedIds = [...new Set(Object.keys(repeatingAttrs)
      .map(n => n.match(regExp))
      .filter(x => !!x)
      .map(a => a[1]))];
    const repRowIds = [...new Set(repOrder.filter(x => unorderedIds.includes(x)).concat(unorderedIds))];
    return [repRowIds, repeatingAttrs];
  }

  function makeButton(name, link, minwidth) {
    const buttonStyle = `background-color: #cccccc25; color:#521E10 !important; font-weight:bold; border-radius: 0px; padding: 0px; margin: 1px 1px 1px 0px; display: inline-block`;  
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

  function imgBorder(url){
    return ((url.includes('marketplace')) ? 'border: 1px solid #000;; ' : 'border: 1px solid #aaa;');
  }

  function makeHelpButton(title, helpText) {
    return `<div style = 'float:right '><a style = 'color: red; display:inline-block; padding:0px; margin:0px; background-color:white; border-radius:8px;' href = '!survey --sendtext|${title}|${helpText}'>&nbsp;?&nbsp;</a></div>`
  }

  function makeBox(color, id, name) {
    return `<a href = '!survey --pcs ${id}' style= 'float: left; display:inline-block; height: 20px; width: 20px;  margin-top: 2px; margin-right: 2px; background-color:${color}; border: 1px solid black; clear: both; !important'</a>`;
  }
  function formatAsPercent(num) {
    return new Intl.NumberFormat('default', {
      style: 'percent',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(num / 100);
  }

  function resolveAttr(cid, attname) {

    // log('debug ra 1' + attname);

    let attobj = findObjs({
      type: 'attribute',
      characterid: cid,
      name: attname
    }, { caseInsensitive: true })[0];

    if (!attobj) {
      return { name: '', current: '', max: '' };
    }
    let att2 = { name: attobj.get('name'), current: attobj.get('current'), max: attobj.get('max') };

    // log('debug ra 2' + att2);
    return att2;
  }

  function getCharMainAtt2(cid2) {
    let tbl = '<table border=0><tr>';
    let z = '';
    let wantedAttributes = ['strength', 'dexterity', 'constitution', 'intelligence', 'wisdom', 'charisma'];
    wantedAttributes.forEach(myAtt => {
      let ca = resolveAttr(cid2, myAtt);
      let caName = ca.name.slice(0, 1).toUpperCase();
      let caVal = ca.current;
      let caMod = resolveAttr(cid2, myAtt + '_mod');
      let caModVal = caMod.current;
      if (caModVal >0){caModVal='+' + caModVal}
      tbl += '<td><strong>' + caName + ':</strong>&nbsp;' + caModVal + '(' + caVal + ')</td>';
      z += caName + '(' + caModVal +')';
    });
    tbl += '</tr></table>';
    return z;
  }

  function changeAttributeValue(characterId, attributeName, newValue, maxValue) {
    // Get the attribute object
    let attribute = findObjs({
      _type: 'attribute',
      characterid: characterId,
      name: attributeName,
    })[0];

    // Check if the attribute exists
    if (attribute) {
      // Update the attribute with the new value
      attribute.set('current', newValue);
    } else {
      // Create the attribute with the specified name and value
      log(`Create Attribute ${attributeName} for ${characterId} value ${newValue} Max ${maxValue}`)      
      if (!maxValue){
        createObj('attribute', {
          characterid: characterId,
          name: attributeName,
          current: newValue
        });
      } else {
      createObj('attribute', {
        characterid: characterId,
        name: attributeName,
        current: newValue,
        max: maxValue
        });
      }
    }
  }

  function getCharacterByName(characterName) {
  // Find the character with the specified name
  let character = findObjs({
    _type: 'character',
    name: characterName,
  })[0];

  // Return the character object or null if not found
  return character || null;
  }

  function getSMImages(sm) {
    // Build an array of Token Status Marker images for later use
    let x = 0;
    let y = 0;
    let smList = sm.split(','); // Split the input string into an array of tags
    let sm_url = '';
    let sm_Images = '';

    // Moved to the top of HandleMsg to improve performance - no need to load campaign markers every time
    //const tokenMarkers = JSON.parse(Campaign().get("token_markers"));

    // Loop through the list of tags
    for (x = 0; x < smList.length; x++) {
      // Loop through the tokenMarkers array
      for (y = 0; y < tokenMarkers.length; y++) {
        // Check if the tag matches an element in the tokenMarkers array
        if (tokenMarkers[y].tag === smList[x]) {
          sm_url = tokenMarkers[y].url; // Get the URL associated with the tag
          break;
        }
      }
      // Concatenate the img element with the corresponding URL
      sm_Images += `<img style='max-height: 20px; max-width: 20px; padding: 0px; margin: 0px !important' src='${sm_url}'></img>`;
    }
    return sm_Images; // Return the final string containing the img elements
  }

  function spc(c) {
    let s = '&nbsp;'; // Declare a non-breaking space string
    let x = 0; // Initialize a counter variable
    let spcs = ''; // Initialize an empty string to store the non-breaking spaces

    // Loop from 1 to c-1
    for (x = 1; x < c; x++) {
      spcs += s; // Append the non-breaking space string to the spcs variable
    }

    return spcs; // Return the final string containing the non-breaking spaces
  }

  function AddSign(v){
    if (v>0){
      v='+' + v;
    }
    return v;
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
        log(`Created new handout: ${handoutName}`);
    }
    return handout;
  }

  const getAttrCountByChar = () => findObjs({
    type: 'attribute'
    })
    .map(o => o.get('characterid'))
    .reduce((m, o) => ({
        ...m,
        [o]: (m[o] || 0) + 1
    }), {});

  const getAbilityCountByChar = () => findObjs({
    type: 'ability'
    })
    .map(o => o.get('characterid'))
    .reduce((m, o) => ({
        ...m,
        [o]: (m[o] || 0) + 1
    }), {}
  );

  function getSystemTimeInSecs()
  {
    let d = new Date();
    s = d.getUTCDate() * 3600 * 24;
    s += d.getUTCHours() * 3600;
    s += d.getUTCMinutes() * 60;
    s += d.getUTCSeconds();
    // log('Date Time: ' + d.getUTCDate() + ' '  + d.getUTCHours() + ':' + d.getUTCMinutes() + ':' + d.getUTCSeconds())
    return s;
  }

  function resetAttributeValue(attr) {
    findObjs({ _type: 'attribute', name: attr }).forEach((attr) => {
      attr.set('current', attr.get('max'));
    });
  }

  function ResetStats(){

    //Reset Player Stats
    // Look for every Attribute named to_secs, to_count or to_avg and set their current value equal to their max value
    resetAttributeValue('to_secs');
    resetAttributeValue('to_count');
    resetAttributeValue('to_avg');

    //Reset DM/State Stats
    state.TOReporter.DM_Secs = 0;
    state.TOReporter.DM_Count = 0;
    state.TOReporter.DM_Avg = 0;
  }

  function randomInteger(min, max) {
    const randomDecimal = Math.random();

    // Scale the random decimal number to the desired range [min, max]
    const scaledRandom = randomDecimal * (max - min + 1) + min;

    // Truncate the decimal part and return the resulting integer
    return Math.floor(scaledRandom);
  }

 function addInitiative(tokenIds) {

    //tokenIds will be a commadelimited list of tokenids
    const aryTokenIds = tokenIds.split(',');

    let turnOrder = JSON.parse(Campaign().get("turnorder"));
    if (!turnOrder){
      turnOrder = [];
    }

    aryTokenIds.forEach(tId => {


      let tType = getTokenType(tId);

      let tObj = getObj("graphic", tId);
      if (!tObj){ 
        return; 
      }

      let cObj = getObj("character", tObj.get('represents'));
      if (!cObj){ 
        return; 
      }

      let d20Roll = randomInteger(1,20);
      let initBonus = getAttrByName(cObj.get('_id'),'initiative_bonus','current');
      initBonus = parseFloat(initBonus.toFixed(2)); // Fix Roll20 issue where init bonus has a lot of significant digits.
      let result = Number(d20Roll) + Number(initBonus)

      let chatMsg = '';      
      if (tType == 'CHAR') {
        chatMsg = `@{${cObj.get('name')}|wtype}&{template:simple} {{rname=^{init-u}}} {{r1=[[${d20Roll}[INIT]+[[${initBonus}]][DEX]]]}} {{normal=1}} @{${cObj.get('name')}|charname_output}`
      } else if (tType == 'NPC') {
        chatMsg = `/w gm @{${cObj.get('name')}|wtype}&{template:npc} @{${cObj.get('name')}|npc_name_flag} {{rname=^{init}}} {{r1=[[${d20Roll}+[[${initBonus}]][DEX]]]}} {{normal=1}} {{type=Initiative}}`
      } else {
        return;
      }
      sendChat('API', chatMsg);
      log(`AddInitiative: tId:${tId} result: ${result} TO-Length:${turnOrder.length}`)
      // Add to the TurnOrder
      let newTOItem = {
        id: tId,
        pr: result,
        custom: '',
        _pageid: Campaign().get("playerpageid"),        
      };

      turnOrder.push(newTOItem);

      to_Remove(tId); // Remove the turnorder item if it already exists.  

      log(`AddInitiative: PageId:${Campaign().get("playerpageid")} TO-Length:${turnOrder.length}`)

    });
    Campaign().set("turnorder", JSON.stringify(turnOrder));
  }
 
  /******************************
  *** Refresh Reports Function ***  
  *******************************/
  function refreshReports() {

    log('refreshReports: Start ');

    // **** Variable Declarations  ****
    const openReport = "<div style='color: #000; border: 1px solid #000; background-color: #EFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 5px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
    const openReportx = `<div style='background-image: linear-gradient(rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.5)), url(https://i.imgur.com/8Mm94QY.png); background-size: 100% 100%; box-shadow: 0 0 3px #fff; display: block; text-align: left; font-size: 13px; padding: 5px; margin-bottom: 2px; color: black; font-family: serif; white-space: pre-wrap; line-height:1.2em; font-style:normal'>`;
    const closeReport = '</div>';
    const openHeader = "<div style='font-weight:bold; color:#fff; background-color:#404040; margin-right:3px; padding:3px;'>"
    const closeHeader = `</div>`;
    const openHeaderInfo = "<div style='font-style:italic; color:#fff; background-color:#404040; margin-right:3px; padding:3px; text-align:right;'>"
    const openChat= `<div style="padding:1px 3px;border: 1px solid #8B4513;background: #eeffee; color: #8B4513; font-size: 80%;"><div style="background-color: #ffeeee;">`;
    const closeChat= `<\div><\div>`;
    const openBox = "<div style='color: #000; border: 1px solid #000; background-color: #FFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 2px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
    const closeBox = '</div>';
    

    let lines = '';
    let sheetURL = 'http://journal.roll20.net/character/';
    let profileURL = 'https://app.roll20.net/users/';
    let handoutURL = 'https://app.roll20.net/handout';
    let tableURL = `!&#10;/roll 1t[`;
    let rows = [];
    let urlCount = 0;
    let dmChar = [];
    let toToken = [];
    let toTkn = [];
    let toChar = [];
    let charbtn = '';
    let tknImg = '';
    let skills = '';
    let repeatingSection = '';
    let repeatingName = '';
    let repeatingValues = [];
    let repeatingValue = 0;
    let repeatingMax = 0;
    let hp = 0;
    let hpmax = 0;
    let hppct = 0;
    let btnAdjHP = '';

    let row =[];
    let repeatingField='';
    let spell_lvls = ['cantrip', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
    let slots = '';
    let slots_remaining = '';

    let i=0;
    let x=0;
    let y=0;
    let tType = '';
    let charSorted = [];
    let characterList = '';
    let charReport = '';
    let toList='';
    let toObj=[];
    let tokenList = '';
    let pcList = '';
    let isnpc = 0;
    let sm_Images='';
    let tt = '';
    let sm = '';
    let cb = '';
    let npc = '';
    let speed = '';
    let pp = '';
    let dt_diff = 0;
    let TO_Avg = 0;
    let TO_Secs = 0;
    let TO_Count = 0;

    // Table Formating Variables  
    let ts = ' style="border: 0px; padding: 0;border-collapse:collapse;border-color:#93a1a1;border-spacing:0" class="tg"';
    let th = ' style="background-color:#657b83;border-color:#93a1a1;border-style:solid;border-width:0px;color:#fdf6e3;font-family:Arial, sans-serif;font-size:12px;font-weight:normal;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let tdbak = ' style="background-color:#DCDACF;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td = ' style="background-color:#EFEBD6;border-color:#EFEBD6;border-style:solid;border-width:0px;color:#521E10;font-family:ScalySansRemake, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td1bak = ' style="background-color:#F9EBEA;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td1 = ' style="background-color:#E1E4C4;border-color:#E1E4C4;border-style:solid;border-width:0px;color:#521E10;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';    
    let tdcustom = ' style="background-color:#BEAD70;border-color:#93a1a1;border-style:solid;border-width:0px;color:#521E10;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';        
    let td2 = ' style="background-color:#BBDEFB;border-color:#93a1a1;border-style:solid;border-width:0px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td3 = ' style="background-color:#BBCAFA;border-color:#93a1a1;border-style:solid;border-width:0px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td4 = ' style="background-color:#DAF7A6;border-color:#93a1a1;border-style:solid;border-width:0px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let tdpc = td;
    let tdnpc = td1;
    let tdbase = tdpc;
    let td_health100 = ' style="background-color:#24CE10;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td_health75 = ' style="background-color:#FFFE00;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td_health50 = ' style="background-color:#FFAC00;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td_health25 = ' style="background-color:#FF5A00;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';
    let td_healthDead = ' style="background-color:#FF0000;border-color:#93a1a1;border-style:solid;border-width:1px;color:#002b36;font-family:Arial, sans-serif;font-size:12px;font-weight:bold;overflow:hidden;padding:1px 1px;text-align:left;vertical-align:top;word-break:normal"';    

    let tr = '';
    let td_hp = '';
    let chartbl = '';
    let charheader = '';
    let chartbl_col1 = '';
    let chartbl_col2 = '';
    let chartbl_col3 = '';
    let chartbl_col4 = '';
    let chartbl_col5 = '';

    // **** Load Objects and Arrays of Objects ****
    let ho_TOReport = getHandout('DM Turnoder List');
    let ho_TOCharSheet = getHandout('DM Character Sheet')


    let tokens = findObjs({
        _type: 'graphic',
        _subtype: 'token',
        // controlledby: '',
        _pageid: Campaign().get('playerpageid')
    }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));

    // Did we advance the turn order?
    let cmd_advance = DidTOAdvance();

    //***************************************************************************************
    //  Build Menu Buttons
    //***************************************************************************************

    // PR, TokenName/Custom, HP/HPMax, AC, PP, Attributes, Speed, Senses, Tooltip, Status
    let cmdRefresh = '&#13;!tor --TOReport';
    let btnRefresh = makeButton('Refresh!', '!tor --TOReport');
    let btnExp = '   ' + makeButton('[+] Expand Current Character Details', '!tor --TOReport expand');
    let btnCps = '   ' + makeButton('[-] Collapse Current Character Details', '!tor --TOReport collapse');

    let btnPrev = makeButton('Prev',`!tor --TO-Prev`);
    let btnNext = makeButton('Next',`!tor --TO-Next`);
    let btnSort = makeButton('Sort',`!tor --TO-Sort`);
    let btnSortWrapped = makeButton('Sort-Wrap',`!tor --TO-SortWrapped`);
    let btnClear = makeButton('Clear',`!tor --TO-Clear`);

    // Look at codifying this logic in this report
    //let btn6 = makeButton('[TO-Countdown]', `!&#13;&#37;{Mule|TurnOrder-AddCountdownItem} ${cmdRefresh}`, 105);
    //let btn7 = makeButton('[TO-Custom]', `!&#13;&#37;{Mule|TurnOrder-AddCustItem} ${cmdRefresh}`, 84);
    //let btn8 = makeButton('[TO-Round]', `!&#13;&#37;{Mule|TurnOrder-RoundCounter} ${cmdRefresh}`, 77);

    let btnRound = makeButton('Round', `!tor --TO-AddRound`);
    let btnCounter = makeButton('Counter', `!tor --TO-AddCountdown ?{Counter direction|Count Down,-1|Count Up,+1} ?{Counter starting position|10} "?{Counter Name|}"`);
    let btnCustom = makeButton('Custom', `!tor --TO-AddCustom ?{Custom turnorder position|10} "?{Custom turnorder Name|}"`);
    let btnResetStats = makeButton('Reset-Stats', `!tor --ResetStats`);
    let btnAddNote = makeButton('GMNote', `!tor --AddGMNote "?{Session GM Note?|}"`)

    let btnD20 = makeButton('D20', `!&#13;/roll d20 `) + makeButton('-w', `!&#13;/gmroll d20 `);
    let btnAdv = makeButton('Adv', `!&#13;/roll 2d20kh1 `) + makeButton('-w', `!&#13;/gmroll 2d20kh1 `);
    let btnDis = makeButton('Dis', `!&#13;/roll 2d20kl1 `) + makeButton('-w', `!&#13;/gmroll 2d20kl1 `);
    let btnD4 = makeButton('D4', `!&#13;/roll d4 `) + makeButton('-w', `!&#13;/gmroll d4 `);
    let btnD6 = makeButton('D6', `!&#13;/roll d6 `) + makeButton('-w', `!&#13;/gmroll d6 `);
    let btnD8 = makeButton('D8', `!&#13;/roll d8 `) + makeButton('-w', `!&#13;/gmroll d8 `);
    let btnD10 = makeButton('D10', `!&#13;/roll d10 `) + makeButton('-w', `!&#13;/gmroll d10 `);
    let btnD12 = makeButton('D12', `!&#13;/roll d12 `) + makeButton('-w', `!&#13;/gmroll d12 `);

    // Look at building logic to dynamically add macros from a Mule, based on a prefix of DMR-
    // let btn1 = makeButton('[Player Macros*]', '!&#13;&#37;{Mule|Player-Macros}', 105);
    // let btn2 = makeButton('[DM Tools]', '!&#13;&#37;{Mule|DM-Tools}', 77);
    // let btn9 = makeButton('[Refresh Markers]', `!&#13;&#37;{Mule|AdjHealthMarkers} ${cmdRefresh}`, 119);
    // let btn10 = makeButton('[Combat*]', `!&#13;&#37;{Mule|Menu-Combat-Macros}`, 63);
    // let btn11 = makeButton('[NPC Tools*]', `!&#13;&#37;{Mule|NPC-Tools}`, 84);
    // let btn12 = makeButton('[AoE*]', `!&#13;&#37;{Mule|AoEMenu}`, 40);
    
    // Build the Menu buttons
    let btns = btnRefresh + " "
    btns += btnNext + " "
    btns += btnPrev + " "
    btns += btnSort + " "
    btns += btnSortWrapped + " "
    btns += btnClear + "  |  " 
    btns += btnRound + " "
    btns += btnCounter + " "
    btns += btnCustom + "  |  " 
    btns += btnD20  + " "
    btns += btnAdv + " "
    btns += btnDis + " "
    btns += btnD4 + " "
    btns += btnD6 + " "
    btns += btnD8+ " "
    btns += btnD10+ " "
    btns += btnD12 + "  |  "  
    btns += btnAddNote + " "
    btns += btnResetStats 
    btns = openBox + btns + closeBox


    
    if(Campaign().get("turnorder")=="") 
      turnorder = [];
    else 
      toObj = JSON.parse(Campaign().get("turnorder"));
    
    // Is there at least a couple entries in the turn order, and we are responding to an 
    // event that advances the turnorder?
    if (toObj.length >0) { 

      // Turnorder Time Report
      let dt1 = state.TOReporter.LastDT1;

      log(`Debug TOAdv 1: cmd_advance:${cmd_advance} dt1:${dt1}`)
      if(isNaN(dt1))
      {
        state.TOReporter.LastDT1 = getSystemTimeInSecs();
        state.TOReporter.LastUTCDate = GetSystemUTCDate();
      } else {
        dt2 = getSystemTimeInSecs();
        state.TOReporter.LastDT1 = dt2;
        state.TOReporter.LastUTCDate = GetSystemUTCDate();
        dt_diff = Math.floor(Math.abs(Number(dt2) - Number(dt1)));

        log(`Debug TOAdv 2: dt_diff:${dt_diff} dt2:${dt2}`)
        if (dt_diff > 10000  || cmd_advance == 0){
          dt_diff=0;
        } else {

          // Avoid stacking up the que procesing tons of report refreshes
          //if (dt_diff <=1){
          //  log('Turnorder Report: Quitting Early to not throttle the API');
          //  return;
          //}

          // If we got here, then we know that a Next Item event was fired from the TO dialog or
          // through the API

          // Get Previous Turn Id and see if it was a character and we have seconds to apply
          if ((dt_diff >= 0) && toObj[toObj.length-1].id != -1) {

            // Yes, Lets see if it's a PC or NPC
            Ttype = getTokenType(toObj[toObj.length-1].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER
            log(`Debug TOAdv 3: Ttype:${Ttype}`)

            // log('TO-PrevTokenType: ' + Ttype);

            switch (Ttype) {

              case 'CHAR':
                let toPrevToken = getObj("graphic", toObj[toObj.length-1].id);
                let toPrevChar = getObj("character", toPrevToken.get('represents'));

                TO_Secs = getAttrByName(toPrevChar.get('_id'),'to_secs','current');
                if (!TO_Secs){TO_Secs=0}

                TO_Count = getAttrByName(toPrevChar.get('_id'),'to_count','current');
                if (!TO_Count){TO_Count=0}

                TO_Avg = getAttrByName(toPrevChar.get('_id'),'to_avg','current');
                if (!TO_Avg){TO_Avg=0}

                y=TO_Secs;

                if (TO_Count >= 50){
                  TO_Count = 50;
                  TO_Secs = Number(TO_Secs) - Number(TO_Avg) + Number(dt_diff);
                } else {
                  TO_Secs = Number(dt_diff) + Number(TO_Secs);
                  TO_Count = Number(TO_Count) + 1;
                }
                log(`TO_Metrics: Cnt(${TO_Count}) OldSecs(${y}) NewSecs(${TO_Secs}) Avg(${TO_Avg}) dt_diff(${dt_diff})`)

                TO_Avg = Math.floor(TO_Secs / TO_Count);
                changeAttributeValue(toPrevChar.get('_id'), 'to_secs', TO_Secs, 3000)
                changeAttributeValue(toPrevChar.get('_id'), 'to_count', TO_Count, 50)
                changeAttributeValue(toPrevChar.get('_id'), 'to_avg', TO_Avg, 60)
                changeAttributeValue(toPrevChar.get('_id'), 'to_lastturn', dt_diff)
                break;

              case 'NPC':  

                // Store NPC turnorder stats in State Memory
                TO_Count = state.TOReporter.DM_Count;
                TO_Secs = state.TOReporter.DM_Secs;
                TO_Avg = state.TOReporter.DM_Avg;

                if (TO_Count >= 50){
                  TO_Count = 50;
                  TO_Secs = TO_Secs - TO_Avg + dt_diff;
                } else {
                  TO_Secs += dt_diff;
                  TO_Count += 1;
                }

                TO_Avg = Math.floor(TO_Secs / TO_Count);
                state.TOReporter.DM_Secs = TO_Secs;
                state.TOReporter.DM_Count = TO_Count;
                state.TOReporter.DM_Avg = TO_Avg;
                break;
            }
          }
        }
      }


      /******************************************************************
      *  Build the Detailed NPC or Character section if expanded        *
      ******************************************************************/

      Ttype = getTokenType(toObj[0].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER

      switch (Ttype) {
        case 'OTHER':

          toToken = getObj("graphic", toObj[0].id);                
          tknImg = `<img style = 'max-height: 50px; max-width: 50px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;
          charheader = `${openHeader} ${makeButton('🎯', `!tor --PingToken-GM ${toToken.get('_id')}`, 20)} ${tknImg} ${toToken.get('name')} - Graphic Token (Not associated with Character/NPC) Turnorder: (${toObj[0].pr})  ${closeHeader}<br>`;
          chartbl = '';
          
          break;

        case 'CUSTOM':
          charheader = `${openHeader}Custom Turnorder: ${toObj[0].custom} (${toObj[0].pr}) [Formula: ${toObj[0].formula}] ${closeHeader}<br>`;
          chartbl = '';
          break;

        case 'CHAR':
          log('TOReport: PC');                                    

          toToken = getObj("graphic", toObj[0].id);
          toChar = getObj("character", toToken.get('represents'));

          isnpc = 0;
          tdbase = tdpc;
          chartbl = '';

          TO_Avg = getAttrByName(toChar.get('_id'),'to_avg','current');
          TO_Secs = getAttrByName(toChar.get('_id'),'to_secs','current');
          TO_Count = getAttrByName(toChar.get('_id'),'to_count','current');


          //Row1 - Column 1-5: Basic Info
          charbtn = makeButton('📑', 'https://journal.roll20.net/character/' + toToken.get('represents'), 20);
          tknImg = `<img style = 'max-height: 50px; max-width: 50px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;
          charheader = `${openHeader} ${charbtn} ${makeButton('🎯', `!tor --PingToken-GM ${toToken.get('_id')}`, 20)} ${tknImg} ${toToken.get('name')}        [Avg Turn: ${TO_Avg} / Secs:  ${TO_Secs} / Cnt: ${TO_Count}] ${closeHeader} <b><i>${getAttrByName(toChar.get('_id'),'alignment','current')} ${getAttrByName(toChar.get('_id'),'class_display','current')} (${getAttrByName(toChar.get('_id'),'race_display','current')})</b></i> `;
          chartbl += '<table><thead><tr><th' + th.slice(0,-1) + ';width:10%">Attributes</th><th' + th.slice(0,-1) + ';width:20%">Skills</th><th' + th.slice(0,-1) + ';width:20%">Stats</th><th' + th.slice(0,-1) + ';width:12%">Attacks</th><th' + th.slice(0,-1) + ';width:13%">Traits</th><th' + th.slice(0,-1) + ';width:25%">Spells/Resources</th></tr></thead><tbody>';

          // Send a "Next Turn" message to the game chat, if this is being refreshed for that purpose
          log('Advance? ' + cmd_advance);

          if (cmd_advance == 1){
            sendChat('Turn Order', `${openChat}<table><tr><td>${tknImg}</td><td><b>${toToken.get('name')} is up!</b><br> (ATPT: ${TO_Avg} Secs / Last turn: ${getAttrByName(toChar.get('_id'),'to_lastturn','current')})</td></tr></table> ${closeChat}`);                
            //sendChat('Debug', `${openChat}<table><tr><td>(Count: ${TO_Count} Secs: ${TO_Secs}</td></tr></table> ${closeChat}`);                
          }

          //Row2 - Column 1: Basic Info
          chartbl += '<tr><td' + tdpc + '>' // 1 Row Table incapsalating columns of info
          chartbl += '<table>'; // Table for Column 1
          chartbl += '<tr><td' + tdpc + '><b>Str:</b></td><td' + tdpc + '>' + getAttrByName(toChar.get('_id'),'strength','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'strength_mod','current')) +')</td><td' + tdpc + '>sv:' + AddSign(getAttrByName(toChar.get('_id'),'strength_save_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Dex:</b></td><td' + tdpc + '>' + getAttrByName(toChar.get('_id'),'dexterity','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_mod','current')) +')</td><td' + tdpc + '>sv:' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_save_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Con:</b></td><td' + tdpc + '>' + getAttrByName(toChar.get('_id'),'constitution','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'constitution_mod','current')) +')</td><td' + tdpc + '>sv:' + AddSign(getAttrByName(toChar.get('_id'),'constitution_save_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Wis:</b></td><td' + tdpc + '>' + getAttrByName(toChar.get('_id'),'wisdom','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_mod','current')) +')</td><td ' + tdpc + '>sv:' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_save_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Int:</b></td><td' + tdpc + '>' + getAttrByName(toChar.get('_id'),'intelligence','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_mod','current')) +')</td><td' + tdpc + '>sv:' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_save_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Cha:</b></td><td' + tdpc + '>' + getAttrByName(toChar.get('_id'),'charisma','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'charisma_mod','current')) +')</td><td' + tdpc + '>sv:' + AddSign(getAttrByName(toChar.get('_id'),'charisma_save_bonus','current')) + '</td></tr>';
          chartbl += '</table></td>' // End of Column 1

          // Basics
          chartbl += '<td' + tdpc + '><table>';
          chartbl += '<tr><td' + tdpc + '><b>AC:</b>' + getAttrByName(toChar.get('_id'),'ac','current') + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>HP:</b>' + getAttrByName(toChar.get('_id'),'hp','current') + '/' + getAttrByName(toChar.get('_id'),'HP','max') + '  Temp:' + getAttrByName(toChar.get('_id'),'hp_temp','current') + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Speed:</b>' + getAttrByName(toChar.get('_id'),'speed','current') + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>PP:</b>' + getAttrByName(toChar.get('_id'),'passive_wisdom','current') + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Init:</b>' + AddSign(getAttrByName(toChar.get('_id'),'initiative_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Caster:</b>' + getAttrByName(toChar.get('_id'),'caster_level','current') + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Spell DC:</b>' + getAttrByName(toChar.get('_id'),'spell_save_dc','current') + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Spell Attack Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'spell_attack_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Prof Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'pb','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>Tools:</b>';                               

          // Define the repeating section identifier
          repeatingSection = 'repeating_tool';  //Prefix
          repeatingName = 'toolname'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // log('Repeating tools Length:' + repeatingValues.length);

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-9);
            chartbl +=  row.get('current') + ', ';
          }
          chartbl += '</td></tr>'; 

          // Need to get
          chartbl += '<tr><td' + tdpc + '><b>Lang/Other:</b>';                               

          // Define the repeating section identifier
          repeatingSection = 'repeating_proficiencies';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            chartbl +=  row.get('current') + ', ';
          }
          chartbl += '</td></tr>'; 
          chartbl += '</table></td>';

          // Skills
          chartbl += '<td' + tdpc + '><table><tr><td' + tdpc + '><table>';
          skills = ['acrobatics', 'animal_handling', 'arcana', 'athletics', 'deception', 'history', 'insight', 'intimidation', 'investigation', 'medicine', 'nature', 'perception', 'performance', 'persuasion', 'religion', 'sleight_of_hand', 'stealth', 'survival'];
          skills.forEach(mySkill => {
              let skill = resolveAttr(toChar.get('_id'), mySkill + '_bonus');
              let skill_value = skill.current;
              let skill_name = mySkill;
              chartbl += '<tr><td' + tdpc + '><b>' + makeButton(skill_name,`!&#13;&#64;{${toChar.get('name')}|${mySkill}_roll}`)+ ':</b>' + AddSign(skill_value) + '</td></tr>';
              if (mySkill == 'investigation'){
                  chartbl += '</table></td><td' + tdpc + '><table>';
              }
          });

          chartbl += '</table></td></table></td>';

          // *******   Attacks *******
          chartbl += '<td' + tdpc + '><table>';

          // Define the repeating section identifier
          repeatingSection = 'repeating_attack';  //Prefix
          repeatingName = 'atkname'               //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            // log('Repeating attack info: ' + rowId + '.' + row.get('name') + ': ' +  row.get('current'));

            repeatingField = row.get('name').slice(0,-8);
            let repeatingAction = repeatingField + '_attack';
            repeatingAction = makeButton(row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`, 125);
            chartbl += '<tr><td' + tdpc + '>' + repeatingAction + '</td></tr>';
          }

          // ******* Traits *******
          chartbl += '</table></td>';
          chartbl += '<td' + tdpc + '><table>';

          // Define the repeating section identifier
          repeatingSection = 'repeating_traits';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingDesc = getAttrByName(toChar.get('_id'),repeatingField + '_description','current')
            let ttdiv = `<div class="showtip" title="${repeatingDesc}">`
            chartbl += '<tr><td' + tdpc + '><b>' + ttdiv + row.get('current') + '</div></b></td></tr>';
          }
          chartbl += '</table></td>';
          chartbl += '<td' + tdpc + '><table>';

          // ************* Spells *******************
          spell_lvls = ['cantrip', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
          slots = '';
          slots_remaining = '';

          spell_lvls.forEach(spell_lvl => {

            // Get Slots and Prepared
            slots = '';
            if (spell_lvl != 'cantrip') {
              slots = getAttrByName(toChar.get('_id'),`lvl${spell_lvl}_slots_total`,'current');
              slots_remaining = getAttrByName(toChar.get('_id'),`lvl${spell_lvl}_slots_expended`,'current');
              slots = `( ${slots} of ${slots_remaining})`;
            }

            repeatingSection = `repeating_spell-${spell_lvl}`;  //Prefix
            repeatingName = 'spellname'                    //Suffix 

            // Get the values of the repeating section
            repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
              .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}`) && attribute.get("name").endsWith(`_${repeatingName}`));
            if (repeatingValues.length > 0){
              chartbl += '<tr><td' + tdpc + '><b>Level ' + spell_lvl + slots + ':</b> ';
              // Loop through the values in the repeating section
              for (let rowId in repeatingValues) {
                row = repeatingValues[rowId];
                repeatingField=row.get('name').slice(0,-10);
                let repeatingAction = repeatingField + '_spell';
                let spellPrepared = 0;
                if (spell_lvl != 'cantrip') { spellPrepared = getAttrByName(toChar.get('_id'),`${repeatingField}_spellprepared`,'current'); }
                
                let spellInnate = getAttrByName(toChar.get('_id'),`${repeatingField}_innate`,'current');
                if (spellInnate.length > 2) { spellInnate = '<i>(' + spellInnate + ')</i>'; } else {spellInnate = ''; }

                repeatingAction = makeButton('🧙', `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`, 10);
                let spelllink = 'https://www.dndbeyond.com/spells/' + row.get('current').replace(/ /g, "-")
                spelllink = makeButton('🔗', spelllink, 15)

                // Build a spell description field here (Range, Action, Target, Duration, Con, Desc)
                if (spell_lvl != 'cantrip') {
                  if (spellPrepared == '1') {
                    chartbl += repeatingAction + spelllink + '<b>' + row.get('current') + '*</b>'+ spellInnate + ', ';  
                  } else {
                    chartbl += repeatingAction + spelllink + row.get('current') + spellInnate + ', ';  
                  }

                } else {
                  chartbl += repeatingAction + spelllink + '<b>' + row.get('current') + '*</b>, ';  
                }

              }
              chartbl = chartbl.slice(0,-2);
              chartbl += '</td></tr>';
            }
          });

          chartbl += '<tr><td' + tdpc + '>  </td></tr><tr><td' + tdpc + '><br><b><u> ***** Resources ***** </u></b> </td></tr>';
          
          // ********** Resources ***************
          // chartbl += '<td' + tdpc + '><table>';
          chartbl += '<tr><td' + tdpc + '><b>' + getAttrByName(toChar.get('_id'),'class_resource_name','current') + '[cr] (' + getAttrByName(toChar.get('_id'),'class_resource','current') + ' of ' + getAttrByName(toChar.get('_id'),'class_resource','max') + ')</td></tr>';
          chartbl += '<tr><td' + tdpc + '><b>' + getAttrByName(toChar.get('_id'),'other_resource_name','current') + '[or] (' + getAttrByName(toChar.get('_id'),'other_resource','current') + ' of ' + getAttrByName(toChar.get('_id'),'other_resource','max') + ')</td></tr>';

          // Define the repeating section identifier
          repeatingSection = 'repeating_resource';  //Prefix
          repeatingName = 'resource_left_name';  //Suffix 
          repeatingValue = 0;
          repeatingMax = 0;

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingName = getAttrByName(toChar.get('_id'),row.get('name'),'current')
            repeatingField = row.get('name').slice(0,-19);
            repeatingValue = getAttrByName(toChar.get('_id'),repeatingField + '_resource_left','current')
            repeatingMax = getAttrByName(toChar.get('_id'),repeatingField + '_resource_left','current')
            chartbl += '<tr><td' + tdpc + '><b>' + row.get('current') + '</b>: (' + repeatingValue + ' of ' + repeatingMax + ')</td></tr>';
          }

          repeatingSection = 'repeating_resource';  //Prefix
          repeatingName = 'resource_right_name';  //Suffix 
          repeatingValue = 0;
          repeatingMax = 0;

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            let row = repeatingValues[rowId];
            let repeatingName = getAttrByName(toChar.get('_id'),row.get('name'),'current')
            let repeatingField = row.get('name').slice(0,-20);
            let repeatingValue = getAttrByName(toChar.get('_id'),repeatingField + '_resource_right','current')
            let repeatingMax = getAttrByName(toChar.get('_id'),repeatingField + '_resource_right','current')
            chartbl += '<tr><td' + tdpc + '><b>' + row.get('current') + '</b>: (' + repeatingValue + ' of ' + repeatingMax + ')</td></tr>';
          }
          chartbl += '</table></td></tr></table>';
          //chartbl += '</table>';
          break;

        case 'NPC':
          log('TOReport: NPC');                                    

          toToken = getObj("graphic", toObj[0].id);
          toChar = getObj("character", toToken.get('represents'));

          isnpc = 1;
          tdbase = tdnpc;
          chartbl = '<table>';

          //Row1 - Column 1-5: Basic Info
          charbtn = makeButton('📑', 'https://journal.roll20.net/character/' + toToken.get('represents'), 20);
          tknImg = `<img style = 'max-height: 50px; max-width: 50px; padding: 0px; margin: 0px !important' src = '${toToken.get('imgsrc')}'</img>`;

          charheader = `${openHeader} ${charbtn} ${makeButton('🎯', `!tor --PingToken-GM ${toToken.get('_id')}`, 20)} ${tknImg} ${toToken.get('name')}      [Avg Turn: ${state.TOReporter.DM_Avg} / Secs: ${state.TOReporter.DM_Secs} / Cnt: ${state.TOReporter.DM_Count}]  ${closeHeader} <b><i>${getAttrByName(toChar.get('_id'),'npc_type','current')}</b></i> `;
          chartbl += '<table><thead><tr><th' + th.slice(0,-1) + ';width:10%">Attributes</th><th' + th.slice(0,-1) + ';width:20%">Stats</th><th' + th.slice(0,-1) + ';width:20%">Info</th><th' + th.slice(0,-1) + ';width:20%">Traits/Actions</th><th' + th.slice(0,-1) + ';width:30%">Spells</th></tr></thead><tbody>';

          //Row2 - Column 1: Basic Info
          chartbl += '<tr><td' + tdnpc + '><table>';
          chartbl += '<tr><td' + tdnpc + '><b>Str:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'strength','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'strength_mod','current')) +')</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Dex:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'dexterity','current')  + '(' + AddSign(getAttrByName(toChar.get('_id'),'dexterity_mod','current')) +')</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Con:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'constitution','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'constitution_mod','current')) +')</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Wis:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'wisdom','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'wisdom_mod','current')) +')</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Int:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'intelligence','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'intelligence_mod','current')) +')</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Cha:</b></td><td' + tdnpc + '>' + getAttrByName(toChar.get('_id'),'charisma','current') + '(' + AddSign(getAttrByName(toChar.get('_id'),'charisma_mod','current')) +')</td></tr>';
          chartbl += '</table></td>'

          // Basics
          chartbl += '<td' + tdnpc + '><table>';
          chartbl += '<tr><td' + tdnpc + '><b>AC:</b>' + getAttrByName(toChar.get('_id'),'npc_ac','current') + ' (' + getAttrByName(toChar.get('_id'),'npc_actype','current') + ')</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>HP:</b>' + getAttrByName(toChar.get('_id'),'hp','current') + '/' + getAttrByName(toChar.get('_id'),'HP','max') + '(' + getAttrByName(toChar.get('_id'),'npc_hpformula','current') + ')</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Speed:</b>' + getAttrByName(toChar.get('_id'),'npc_speed','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>PP:</b>' + getAttrByName(toChar.get('_id'),'passive_wisdom','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Init:</b>' + AddSign(getAttrByName(toChar.get('_id'),'initiative_bonus','current')) + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Caster:</b>' + getAttrByName(toChar.get('_id'),'caster_level','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Spell DC:</b>' + getAttrByName(toChar.get('_id'),'spell_save_dc','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Spell Attack Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'spell_attack_bonus','current')) + '</td></tr>';

          chartbl += '</table></td>';

          // Skills
          chartbl += '<td' + tdnpc + '><table>';
          chartbl += '<tr><td' + tdnpc + '><b>Skills:</b>';
          skills = ['acrobatics', 'animal_handling', 'arcana', 'athletics', 'deception', 'history', 'insight', 'intimidation', 'investigation', 'medicine', 'nature', 'perception', 'performance', 'persuasion', 'religion', 'sleight_of_hand', 'stealth', 'survival'];
          skills.forEach(mySkill => {
            let skill = resolveAttr(toChar.get('_id'), 'npc_' + mySkill + '_base');
            let skill_value = skill.current;
            if (skill_value != 0) {
              let myRoll = `!&#13;?&#64;{${toChar.get('name')}|wtype}&amp;&#123;template:npc&#125;&nbsp;&#64;{${toChar.get('name')}|npc_name_flag}&nbsp;&#64;{Noble|rtype&#125;+[[@{${toChar.get('name')}|npc_${mySkill}}]]]]&#125;&#125; {{r1=[[@{${toChar.get('name')}|d20}+[[@{${toChar.get('name')}|npc_${mySkill}}]]]]&#125;&#125; {{mod=[[[[@{${toChar.get('name')}|npc_${mySkill}}]]]]&#125;&#125; {{rname=${mySkill}&#125;&#125; {{type=Skill&#125;&#125;`
              chartbl += makeButton(mySkill, myRoll) + ' ' + skill_value + ', ';
            }
          });
          chartbl += '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Dmg Res:</b>' + getAttrByName(toChar.get('_id'),'npc_resistances','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Dmg Imm:</b>' + getAttrByName(toChar.get('_id'),'npc_immunities','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Cnd Imm:</b>' + getAttrByName(toChar.get('_id'),'npc_condition_immunities','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Dmg Vul:</b>' + getAttrByName(toChar.get('_id'),'npc_vulnerabilities','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Lanuages:</b>' + getAttrByName(toChar.get('_id'),'npc_languages','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Senses:</b>' + getAttrByName(toChar.get('_id'),'npc_senses','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>CR:</b>' + getAttrByName(toChar.get('_id'),'npc_challenge','current') + '</td></tr>';
          chartbl += '<tr><td' + tdnpc + '><b>Prof Bonus:</b>' + AddSign(getAttrByName(toChar.get('_id'),'npc_pb','current')) + '</td></tr>';
          chartbl += '</table></td>';
          chartbl += '<td' + tdnpc + '><table>';

          // Define the repeating section identifier
          repeatingSection = 'repeating_npctrait';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            // log('Repeating Traits info: ' + rowId + '.' + row.get('name') + ': ' +  row.get('current'));

            repeatingField=row.get('name').slice(0,-5);
            let repeatingDesc = getAttrByName(toChar.get('_id'),repeatingField + '_description','current')
            let ttdiv = `<div class="showtip" title="${repeatingDesc}">`
            chartbl += '<tr><td' + tdnpc + '>' + ttdiv + 'T: ' + row.get('current') + '</div></td></tr>';
          }

          // ******* Actions *******
          // Define the repeating section identifier
          repeatingSection = 'repeating_npcaction';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingAction = repeatingField + '_npc_action';
            repeatingAction = makeButton('A: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`, 200);
            chartbl += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';
          }

          // ***  Bonus Actions *** 
          // Define the repeating section identifier
          repeatingSection = 'repeating_npcbonusaction';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingAction = repeatingField + '_npc_action';
            repeatingAction = makeButton('BA: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`, 200);
            chartbl += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';
          }

          // Reactions
          repeatingSection = 'repeating_npcreaction';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingAction = repeatingField + '_npc_action';
            repeatingAction = makeButton('RA: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`, 200);
            chartbl += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';
          }

          // Legendary Actions
          repeatingSection = 'repeating_npcaction-l';  //Prefix
          repeatingName = 'name'                    //Suffix 

          // Get the values of the repeating section
          repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
            .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}_`) && attribute.get("name").endsWith(`_${repeatingName}`));

          // Loop through the values in the repeating section
          for (let rowId in repeatingValues) {
            row = repeatingValues[rowId];
            repeatingField=row.get('name').slice(0,-5);
            let repeatingAction = repeatingField + '_npc_action';
            repeatingAction = makeButton('LA: ' + row.get('current'), `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`, 200);
            chartbl += '<tr><td' + tdnpc + '>' + repeatingAction + '</td></tr>';
          }

          // ************* Spells *******************
          spell_lvls = ['cantrip', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
          chartbl += '</table></td>';
          chartbl += '<td' + tdnpc + '><table>';

          spell_lvls.forEach(spell_lvl => {

            repeatingSection = `repeating_spell-${spell_lvl}`;  //Prefix
            repeatingName = 'spellname'                    //Suffix 

            // Get the values of the repeating section
            repeatingValues = findObjs({_type: "attribute", _characterid: toChar.get('_id')})
              .filter(attribute => attribute.get("name").startsWith(`${repeatingSection}`) && attribute.get("name").endsWith(`_${repeatingName}`));
            if (repeatingValues.length > 0){
              chartbl += '<tr><td' + tdnpc + '><b>Level ' + spell_lvl + ':</b> ';
              // Loop through the values in the repeating section
              for (let rowId in repeatingValues) {
                row = repeatingValues[rowId];
                repeatingField=row.get('name').slice(0,-10);
                let repeatingAction = repeatingField + '_spell';
                repeatingAction = makeButton('🧙', `!&#13;&#37;{${toChar.get('_id')}|${repeatingAction}}`, 10);

                let spelllink = 'https://www.dndbeyond.com/spells/' + row.get('current').replace(/ /g, "-")
                spelllink = makeButton('🔗', spelllink, 15)

                // Build a spell description field here (Range, Action, Target, Duration, Con, Desc)
                chartbl += repeatingAction + spelllink + row.get('current') + ', ';
              }
              chartbl = chartbl.slice(0,-2);
              chartbl += '</td></tr>';
            }
          });
          chartbl += '</table></td></tr></table>';
          // chartbl += '</table>';
          break;
      } // End of switch case for token type
    } // End Testing for empty Turnorder

    // Logic to expand or collapse the Character information
    if (state.TOReporter.DetailExpand == 1) {
      toList = btns + '<br>' + btnCps + charheader + chartbl;
    } else {
      toList = btns + '<br>'+  btnExp;                    
    }
    charReport = openReport + charheader + chartbl + btns + closeReport;
    toList += `<table style="border: 0px; padding: 0;background-color:#404040; border-collapse: collapse;"><tr><td style="border: 0px; padding: 0;">${openHeader}Turn Order${closeHeader}</td><td style="border: 0px; padding: 0;">${openHeaderInfo}(Last Turn: ${dt_diff})${closeHeader}</td></tr></table>`;
    toList +=  '<table ' + ts +'>';
    toList +=  '<thead><tr><th' + th + '>Turn</th>';
    toList +=  '<th' + th + '>Name</th>';
    toList +=  '<th' + th + '>Functions</th>';
    toList +=  '<th' + th + '>HP</th>';
    toList +=  '<th' + th + '>Markers</th>';                
    toList +=  '<th' + th + '>AC</th>';
    toList +=  '<th' + th + '>PP</th>';
    toList +=  '<th' + th + '>Speed</th>';
    toList +=  '<th' + th + '>Senses/Saves</th>';
    toList +=  '<th' + th + '>Tootltip</th>';
    toList +=  '</tr></thead><tbody>';

    /******************************************************************
    *  Build the table of items on the Turnorder list                 *
    ******************************************************************/
    // For each item in the Turnorder
    for (i=0;i<toObj.length;i++) {
      
      toList += '<tr>';

      Ttype = getTokenType(toObj[i].id); // Returns NPC, CHAR, CUSTOM, UTILITY or OTHER
      isnpc = 0;
      tdbase = tdpc;

      switch (Ttype) {
        case 'OTHER':
          toTkn = getObj("graphic", toObj[i].id);
          if (toTkn){
            tknImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${toTkn.get('imgsrc')}'</img>`;

            // Col 1 (Turn/PR)
            toList += '<td ' + tdcustom + '><b>' + toObj[i].pr + '</b></td>';

            // Col 2 (Name/Img)
            tknImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${toTkn.get('imgsrc')}'</img>`;
            if(toTkn.get('layer') == 'objects'){
              toList += '<td ' + tdcustom + '>' + makeButton(tknImg, `!tor --PingToken-GM ${toTkn.get('_id')}`, 40) + '<b>' + toTkn.get('name') + '</b></td>';
            } else {
              toList += '<td ' + tdcustom + '>' + makeButton(tknImg, `!tor --PingToken-GM ${toTkn.get('_id')}`, 40) + '<i>' + toTkn.get('name') + '</i></td>';
            }

            // Col 3 (Functions: Remove Item, Toggle Token between GM/Obj Layer)
            toList +=  '<td ' + tdcustom + '>' + makeButton('❌',`!tor --TO-Remove ${toObj[i].id}`,20);
            if(toTkn.get('layer') == 'objects'){
              toList += makeButton('🙈', '!tor --TokenToggleVisabity ' + toTkn.get('_id')); 

            } else {
              toList += makeButton('🙉', '!tor --TokenToggleVisabity ' + toTkn.get('_id')); 
            }

            if(toTkn.get('lockMovement')){
              toList += makeButton('🔓', '!tor --TokenToggleLock ' + toTkn.get('_id')); 
            } else {
              toList += makeButton('🔐', '!tor --TokenToggleLock ' + toTkn.get('_id')); 
            }

            toList+='</td>'

            // Col 4 (Health)
            hp = toTkn.get('bar1_value');
            hpmax = toTkn.get('bar1_max');
            hp_pct = hp/hpmax * 100;
            hp_pct = hp_pct.toFixed(0);

            td_hp = td_health100;
            if (hp_pct < 75) {td_hp=td_health75;} // Yellow
            if (hp_pct < 50) {td_hp=td_health50;} // Orange
            if (hp_pct < 25) {td_hp=td_health25;} // Red 
            if (hp_pct <= 0) {td_hp=td_healthDead;} // Dead

            // Button to enable adjusting Hit Points
            btnAdjHP = makeButton('🩹',`!tor --TokenAdjustHP  ${toTkn.get('_id')} ?{Adjust HP?|}`);
            toList += '<td ' + td_hp + '>'+ btnAdjHP + hp + ' / ' + hpmax + ' (' + hp_pct + '%)</td>';
            
            // COL 5 Status Markers (Span 5 (5-9))
            sm = toTkn.get('statusmarkers');
            sm_Images = getSMImages(toTkn.get('statusmarkers'));
            toList += '<td ' + tdbase + ' colspan=5>' + sm_Images + '</td>'; //StatusMarkers

            // Col 10 (Tooltips)
            tt = toTkn.get('tooltip');
            toList += '<td ' + tdbase + '>' 
            toList += makeButton('🖊', `!tor --TokenSetTooltip ${toTkn.get('_id')} "?{Edit Tooltip|${tt}}"`) 
            toList += makeButton('❌', '!tor --TokenClearTooltip ' + toTkn.get('_id')) 

            if(toTkn.get('show_tooltip')){
              toList += makeButton('🙈', '!tor --TokenToggleTooltip ' + toTkn.get('_id')) 
              toList += '<b>' + tt + '</b></td>';               
            } else {
              toList += makeButton('🙉', '!tor --TokenToggleTooltip ' + toTkn.get('_id')) 
              toList += '<i>' + tt + '</i></td>'; 
            }
            

          }
          break;

        case 'CUSTOM':
          toList +=  '<td ' + tdcustom + '><b>' + toObj[i].pr + '</b></td>';
          toList +=  '<td ' + tdcustom + '>' + toObj[i].custom + ' [' + toObj[i].formula + ']' + '</td>';
          toList +=  '<td ' + tdcustom + '>' + makeButton('❌',`!tor --TO-RemoveCustom ${i}`,20) + '</td>';
          toList +=  '<td ' + tdcustom + ' colspan=8></td>';
          break;
        case 'NPC':
          isnpc = 1;
          tdbase = tdnpc;
          // NO break on purpose

        case 'CHAR':

          toTkn = getObj("graphic", toObj[i].id);
          toChar = getObj('character', toTkn.get('represents'));

          // Col 1 (Turn/PR)
          toList += '<td ' + tdbase + '><b>' + toObj[i].pr + '</b></td>';

          // Col 2 (Name)
          tknImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${toTkn.get('imgsrc')}'</img>`;

          if(toTkn.get('layer') == 'objects'){
            // toList += '<td ' + tdbase + '>' + makeButton(tknImg, '!ping-token --' + toTkn.get('_id'), 40) + '<b>' + toTkn.get('name') + '</b></td>';
            toList += '<td ' + tdbase + '>' + makeButton(tknImg, `!tor --PingToken-GM ${toObj[i].id}`, 40) + '<b>' + toTkn.get('name') + '</b></td>';                
          } else {
            // toList += '<td ' + tdbase + '>' + makeButton(tknImg, '!ping-token --' + toTkn.get('_id'), 40) + '<i>' + toTkn.get('name') + '</i></td>';
            toList += '<td ' + tdbase + '>' + makeButton(tknImg, `!tor --PingToken-GM ${toObj[i].id}`, 40) + '<i>' + toTkn.get('name') + '</i></td>';                
          }

          // Col 3 (Commands)
          toList += '<td ' + tdbase + '>';
          toList += makeButton('❌',`!tor --TO-Remove ${toObj[i].id}`);

          if(toTkn.get('layer') == 'objects'){
            toList += makeButton('🙈', '!tor --TokenToggleVisabity ' + toTkn.get('_id'));  
          } else {
            toList += makeButton('🙉', '!tor --TokenToggleVisabity ' + toTkn.get('_id'));
          }

          toList += makeButton('📑', 'https://journal.roll20.net/character/' + toTkn.get('represents'));
          if (isnpc) {
            toList += makeButton('🖊', '!tor --AddTokenGMNote ' + toTkn.get('_id') + ' "?{Token GM Note?|}"');  
          } else {
            toList += makeButton('🖊', '!tor --AddCharGMNote ' + toTkn.get('represents') + ' "?{Character GM Note?|}"');
          }

          //log('LockMovement' + toTkn.get('lockMovement'))
          if(toTkn.get('lockMovement')){
            toList += makeButton('🔓', '!tor --TokenToggleLock ' + toTkn.get('_id')); 
          } else {
            toList += makeButton('🔐', '!tor --TokenToggleLock ' + toTkn.get('_id')); 
          }
          
          toList += '</td>';

          // Col 4 (Health)
          hp = toTkn.get('bar1_value');
          hpmax = toTkn.get('bar1_max');
          hp_pct = hp/hpmax * 100;
          hp_pct = hp_pct.toFixed(0);

          td_hp = td_health100;
          if (hp_pct < 75) {td_hp=td_health75;} // Yellow
          if (hp_pct < 50) {td_hp=td_health50;} // Orange
          if (hp_pct < 25) {td_hp=td_health25;} // Red 
          if (hp_pct <= 0) {td_hp=td_healthDead;} // Dead

          btnAdjHP = makeButton('🩹',`!tor --TokenAdjustHP  ${toTkn.get('_id')} ?{Adjust HP?|}`);
          toList = toList + '<td ' + td_hp + '>'+ btnAdjHP + hp + ' / ' + hpmax + ' (' + hp_pct + '%)</td>';
          
          // COL 5 Status Markers
          sm = toTkn.get('statusmarkers');
          sm_Images = getSMImages(toTkn.get('statusmarkers'));

          toList += '<td ' + tdbase + '>' + sm_Images + '</td>'; //StatusMarkers

          // Col 6 (AC)
          toList += '<td ' + tdbase + '>🛡' + toTkn.get('bar2_value') + '</td>'; //AC

          // Col 7 (Passive Perception)
          pp = getAttrByName(toChar.get('_id'),'passive_wisdom','current');                        
          toList += '<td ' + tdbase + '>👀' + pp + '</td>'; //PP

          // Get NPC specific data if this is an NPC
          if (isnpc) {
            // Yes - Get NPC based fields
            speed = getAttrByName(toChar.get('_id'),'npc_speed','current');
            senses = getAttrByName(toChar.get('_id'),'npc_senses','current');
          } else {
            speed = getAttrByName(toChar.get('_id'),'speed','current');
            if (toTkn.get('has_night_vision')){
              senses ='darkvision ' + toTkn.get('night_vision_distance') + ' ft.';
            } else {
              senses ='';
            }
          }

          // Col 8 (Speed)
          toList += '<td ' + tdbase + '>👣' + speed + '</td>'; //Speed

          // Col 9 (Senses)
          toList += '<td ' + tdbase + '><table style="border: 0px; padding: 0;background-color:#404040; border-collapse: collapse;"><tr style="border: 0px; padding: 0;><td ' + tdbase + '>' + senses + '</td>'; //Senses

          let btnSaves = makeButton('Str', `!&#13;&#64;{${toChar.get('name')}|strength_save_roll}`) + ' '
          btnSaves += makeButton('Dex', `!&#13;&#64;{${toChar.get('name')}|dexterity_save_roll}`) + ' '
          btnSaves += makeButton('Con', `!&#13;&#64;{${toChar.get('name')}|constitution_save_roll}`) + ' '
          btnSaves += makeButton('Wis', `!&#13;&#64;{${toChar.get('name')}|wisdom_save_roll}`) + ' '
          btnSaves += makeButton('Int', `!&#13;&#64;{${toChar.get('name')}|intelligence_save_roll}`) + ' '
          btnSaves += makeButton('Cha', `!&#13;&#64;{${toChar.get('name')}|charisma_save_roll}`)

          toList += '<tr><td ' + tdbase + '>' +  btnSaves + '</td></tr></table></td>'; //Saves
          
          //toList += '<td ' + tdbase + '>' + senses + '</td>'; //Senses

          // Col 10 (Tooltips)
          tt = toTkn.get('tooltip');
          toList += '<td ' + tdbase + '>';
          toList += makeButton('🖊', `!tor --TokenSetTooltip ${toTkn.get('_id')} "?{Edit Tooltip|${tt}}"`) 
          toList += makeButton('❌', '!tor --TokenClearTooltip ' + toTkn.get('_id')) 

          if(toTkn.get('show_tooltip')){
            toList += makeButton('🙈', '!tor --TokenToggleTooltip ' + toTkn.get('_id')) 
            toList += '<b>' + tt + '</b></td>'; 
          } else {
            toList += makeButton('🙉', '!tor --TokenToggleTooltip ' + toTkn.get('_id')) 
            toList += '<i>' + tt + '</i></td>'; 
          }

          break;
      } // End switch (token type)

      toList += '</tr>';
    }

    toList += '</tbody></table>';
    // lines = lines + toList;

    let toStr = Campaign().get("turnorder");
    // log('test end 1');

    //*******************************************************************
    // Build a list of NPC and PC tokens that aren't on the TurnOrder list
    // with functionality to add them in groups (PC/NPC) or individually
    //*******************************************************************
    let btnNPCs = '';
    let btnPCs = '';
    let btnAddAllNPCs = '';
    let btnAddAllPCs = '';

    tokens.forEach(t => {
      tType = getTokenType(t.get('_id'));
      // log('BuildTokenList: ' + t.get('name') + ' - ' + tType);
      if (tType == 'NPC' || tType == 'CHAR'){
        let ndx = toStr.indexOf(t.get('_id'));
        if(ndx<0){
          let tImg = `<img style = 'max-height: 40px; max-width: 40px; padding: 0px; margin: 0px !important' src = '${t.get('imgsrc')}'</img>`;
          let btn_ping = makeButton(tImg, `!tor --PingToken-GM ${t.id}`);
          if (tType == 'NPC') {
            btnNPCs += btn_ping + t.get('name') + makeButton(' [+] ', `!tor --Initiative ${t.get('_id')}`);
            btnAddAllNPCs += t.get('_id') + ',';
          } else {
            btnPCs += btn_ping + t.get('name') + makeButton(' [+] ', `!tor --Initiative ${t.get('_id')}`);
            btnAddAllPCs += t.get('_id') + ',';
          }
        }
      }
    });

    btnAddAllNPCs = btnAddAllNPCs.replace(/,([^,]*)$/, '$1'); // Remove last comma
    btnAddAllNPCs = '!tor --Initiative ' + btnAddAllNPCs;
    btnAddAllNPCs = makeButton(' [+] ', btnAddAllNPCs ,20) + '  ';

    btnAddAllPCs = btnAddAllPCs.replace(/,([^,]*)$/, '$1');
    btnAddAllPCs = '!tor --Initiative ' + btnAddAllPCs;
    btnAddAllPCs = makeButton(' [+] ', btnAddAllPCs ,20) + '  ';

    toList += btns + '<b><u>NPCs</u></b>' + btnAddAllNPCs + '<br>' + btnNPCs + '<br><b><u>Player Characters</u></b>' + btnAddAllPCs + '<br>' + btnPCs + '<br>';


    toList = openReport + toList + closeReport;

    toList += "<div style='font-style:italic; color:#fff; margin-right:3px; padding:3px; text-align:right;'>" + makeButton('Turnorder Character Sheet', 'https://journal.roll20.net/handout/' + ho_TOCharSheet.get('_id')) + '  |  ';
    toList += makeButton('Turnorder Log', 'https://journal.roll20.net/handout/' + getNoteLog().get('_id')) + '</div>';


    // Time to write out the results back to a handout.
    if (toList) {
      ho_TOReport.get("notes", function(notes) {
        setTimeout(()=>ho_TOReport.set("notes", toList),0);
      });
    }

    if (charReport) {
      charReport += "<div style='font-style:italic; color:#fff; margin-right:3px; padding:3px; text-align:right;'>" + makeButton('Turnorder List', 'https://journal.roll20.net/handout/' + ho_TOReport.get('_id')) + '  |  ';
      charReport += makeButton('Turnorder Log', 'https://journal.roll20.net/handout/' + getNoteLog().get('_id')) + '</div>';
      ho_TOCharSheet.get("notes", function(notes) {
        setTimeout(()=>ho_TOCharSheet.set("notes", charReport),0);
      });
    }

    log('TO Report end');
    return;
  };


  // Load them at the start to improve performance in the 
  const tokenMarkers = JSON.parse(Campaign().get("token_markers"));

  // Parse Args and Commands
  let args = msg_content.split(/\s--/);
  //let commands = args[1].split(/\s+/);
  let commands = [];
  if (args.length > 1) {
    commands = args[1].match(/(?:[^\s"']+|"[^"]*")+/g);
    commands = commands.map(item => item.replace(/^"|"+$/g, ''));
  } else {
    commands.push('OPEN');
  }

  log('Message_Content: ' + msg_content);
  log('commands: ' + commands);
  commands.forEach(c => {
    log(`  command: ${c}`)
  });

  // Process Commands
  // commands.forEach(c => {

    commands[0] = commands[0].toUpperCase();
    log('Command: ' + commands[0]);
    switch (commands[0]) {

      case 'TO-CLEAR':
        to_Clear();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-NEXT':
        to_MoveNext();
        refreshReports();
        break;
      case 'TO-PREV':
        to_MovePrev();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-SORT':
        to_Sort();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-SORTWRAPPED':
        to_SortWrapped();
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-ADDCUSTOM': // Position, Name
        to_AddCustom(commands[2], commands[1])
        refreshReports();
        break;
      case 'TO-ADDROUND': //No Parameters
        log(`TO-AddRound`)
        to_AddCustom('>>>Round<<<', 1, '+1')
        refreshReports();
        break;
      case 'TO-ADDCOUNTDOWN': // Direction, Starting Pos, Name
        log(`TO-AddCustom: Cmd1:${commands[1]} Cmd2:${commands[2]} Cmd3:${commands[3]} Cmd4:${commands[4]}`)
        to_AddCustom(commands[3], commands[2], commands[1])
        refreshReports();
        break;
      case 'TO-REMOVE':
        to_Remove(commands[1]); // Assumes that the next the command is "!tor --to-Remove tid"
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'TO-REMOVECUSTOM':
        to_RemoveCustom(commands[1]); // Assumes that the next the command is "!tor --to-RemoveCustom ndx"
        updateTurnOrderStartTime();
        refreshReports();
        break;
      case 'ADDCHARGMNOTE': // charId, Name
        addTextToCharGMNote(commands[1], commands[2])
        refreshReports();
        break;
      case 'ADDTOKENGMNOTE': // tokenId, Name
        addTextToTokenGMNote(commands[1], commands[2])
        refreshReports();
        break;
      case 'ADDGMNOTE': // Add note to GMNote are of the Turnorder List
        addTextToGMNote(commands[1])
        break;
      case 'RESETSTATS':
        ResetStats();
        break;
      case 'INITIATIVE':
        addInitiative(commands[1]);
        refreshReports();
        break;
      case 'TOKENTOGGLEVISABITY':
        tokenToggleVisibility(commands[1])
        refreshReports();
        break;
      case 'TOKENTOGGLELOCK':
        tokenToggleLock(commands[1])
        refreshReports();
        break;
      case 'TOKENCLEARTOOLTIP':
        tokenClearTooltip(commands[1])
        refreshReports();
        break;
      case 'TOKENTOGGLETOOLTIP':
        tokenToggleTooltip(commands[1])
        refreshReports();
        break;
      case 'TOKENCLEARTOOLTIP':
        tokenClearTooltip(commands[1])
        refreshReports();
        break;
      case 'TOKENSETTOOLTIP':
        tokenSetTooltip(commands[1], commands[2])
        refreshReports();
        break;
      case 'TOKENADJUSTHP':
        tokenAdjHP(commands[1], commands[2]);
        refreshReports();
        break;
      case 'PINGTOKEN-GM':
        pingToken(commands[1],0);
        break;
      case 'PINGTOKEN-ALL':
        pingToken(commands[1],1);
        break;
      case 'TOREPORT':
        // *** Process subcommands like 'expand' and 'collapse' ***
        let Exp_ndx = msg_content.indexOf('expand');
        if(Exp_ndx>0){
          state.TOReporter.DetailExpand = 1;
        }
        Exp_ndx = msg_content.indexOf('collapse');
        if(Exp_ndx>0){
          state.TOReporter.DetailExpand = 0;
        }
        refreshReports();
        break;
      case 'Open':
        refreshReports();
        const openChat= `<div style="padding:1px 3px;border: 1px solid #8B4513;background: #eeffee; color: #8B4513; font-size: 80%;"><div style="background-color: #ffeeee;">`;
        const closeChat= `<\div><\div>`;
        let chatMsg = `/w gm ${openChat}[Click to open DM Turnorder List handout](https://journal.roll20.net/handout/${getHandout('DM Turnoder List').get('_id')})`;
        chatMsg += `<br>[Click to open Character Sheet handout](https://journal.roll20.net/handout/${getHandout('DM Character Sheet').get('_id')})`
        chatMsg += `<br>[Click to open Turnorder Log handout](https://journal.roll20.net/handout/${getHandout('DM Turnorder Log').get('_id')})${closeChat}`

        sendChat("API", chatMsg);
        break;
      default:
    }
  //});
}

{try{throw new Error('');}catch(e){API_Meta.TOReporter.lineCount=(parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/,'$1'),10)-API_Meta.TOReporter.offset);}}