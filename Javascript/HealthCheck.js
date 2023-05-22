var API_Meta = API_Meta || {};
API_Meta.myTokenEvents = { offset: Number.MAX_SAFE_INTEGER, lineCount: -1 };
{
  try { throw new Error(''); } catch (e) { API_Meta.myTokenEvents.offset = (parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/, '$1'), 10) - (4)); }
}

let gDebug = 1
let gDebugLvl  = 4

on('ready', () => {
  const version = '0.0.01';
  log('HealthCheck ' + version + ' is ready! --offset '+ API_Meta.myTokenEvents.offset);

  if(!state.myTokenEvents) {
    state.myTokenEvents = {
        version: version,
        followData: []
    };
  };

  on('change:graphic:bar1_value', function(obj, prev) {
    // log('DM Dashboard Event: change:graphic:bar1_value');
    setHealthIndicator(obj)
  });

  on('change:graphic:bar1_max', function(obj, prev) {
    // log('DM Dashboard Event: change:graphic:bar1_value');
    setHealthIndicator(obj)   
  });

  on('change:graphic:left', function(obj, prev) {
    let followers = getFollowers(obj.get('_id'));
    followers.forEach(f => {
      moveFollower(obj.get('_id'), f.sTokenid)
    });

    let master = getMaster(obj.get('_id'));
    if (master !== undefined) {
      moveMaster(master.mTokenid, obj.get('_id'))
    }

  });

  on('change:graphic:top', function(obj, prev) {
    let followers = getFollowers(obj.get('_id'));
    followers.forEach(f => {
      moveFollower(obj.get('_id'), f.sTokenid)
    });

    let master = getMaster(obj.get('_id'));
    if (master !== undefined) {
      moveMaster(master.mTokenid, obj.get('_id'))
    }


  });

  on('chat:message', async (msg_orig) => {
    let msg = _.clone(msg_orig);
    if (!/^!Follow/i.test(msg.content) && !/^!HealthCheck/i.test(msg.content) && !/^!DMTools/i.test(msg.content)) {
      return;
    }
    handleMsg(msg);
    //debounced_HandleMsg(msg);

  })

//  const debounced_handleMsg = _.debounce(handleMsg(msg),100)

  const smH1 = 'HealthLvl1::4960092';
  const smH2 = 'Healthlvl2::4960093'
  const smH3 = 'HealthLvl3::4960094'
  const smH4 = 'HealthLvl4::4960095'
  const smDead = 'dead'
  let hpPct = 0;

  function handleMsg(msg){

    let args = msg.content.split(/\s--/);
    let commands = [];
    if (args.length > 1) {
      commands = args[1].match(/(?:[^\s"']+|"[^"]*")+/g);
      commands = commands.map(item => item.replace(/^"|"+$/g, ''));
    } else {

    }
  
    let selected = msg.selected
    if (selected === undefined || selected.length == 0){
      log(`MsgHandler: no token selected`)
    } else {
      log(`MsgHandler: selected: ${selected[0]._id}`)
    }

    if (commands.length > 0) {
      commands[0] = commands[0].toUpperCase();
    }
    let masterCmd = args[0].toUpperCase()
  
    if (masterCmd =='!FOLLOW') {
      switch (commands[0]) {
        case 'ADD':
        case 'EDIT':
            let leftOffset = 35;
            let topOffset = 35;
            if (commands[3] !== undefined) leftOffset = commands[3];
            if (commands[4] !== undefined) topOffset = commands[4];
            addFollower(commands[1], commands[2], leftOffset, topOffset)
            dumpFollowData()
          break;
        case 'DELETE':
          delFollower(commands[1], commands[2])
          break;
        case 'SHOW':
        case 'SHOWFOLLOWERS':
          showFollowers();
          dumpFollowData();
          break;
        case 'DUMPDATA':
          dumpFollowData();
          break;
        case 'CLEAR':
          state.myTokenEvents.followData = [];
          break;

        case 'DIALOG':
        default:
          buildUserDialog(selected[0]._id)
          break;

      }
    } else if (masterCmd == '!HEALTHCHECK') {
      setHealthIndicatorsForPage();
    } else if (masterCmd == '!DMTOOLS') {
      buildDMToolsDialog();
    }
  }

  function addFollower(mTokenid, sTokenid, leftOffset, topOffset){

    let fRec = {};
    // Add a new follower.  If a records already exists, then just update the offsets
    if (leftOffset == undefined) {
      leftOffset = 0;
    }
    if (topOffset == undefined) {
      topOffset = 0;
    }

    let mName = getObj('graphic', mTokenid).get('name');
    let fName = getObj('graphic', sTokenid).get('name');
    let pageid = Campaign().get("playerpageid")
   // log(`adding follower: pid:${pageid} mtid:${mTokenid} stid:${sTokenid} lo:${leftOffset} to:${topOffset}`)

    // Lets see if the item we are adding already exists
    let item = getFollower(mTokenid, sTokenid)
   // log('Follow Item: ' + item)

    // Did we find a record?
    if (item === undefined || item.length === 0){

      // No - We also need to see if the selected folower is following anyone other token.
      item = getMaster(sTokenid)
      if (item === undefined || item.length === 0){ 

        // Final test - This Master Token can't also be a Follower of the targeted token
        item = getFollower(sTokenid, mTokenid);
        if (item === undefined || item.length === 0) {
          fRec = {'pageid':pageid, 'mTokenid':mTokenid, 'sTokenid':sTokenid, 'leftOffset': leftOffset, 'topOffset': topOffset}
          state.myTokenEvents.followData.push(fRec);
          let myMsg = `<b>${mName}</b> has a new follower: <b>${fName}</b>.`
          msgbox({msg: myMsg, title: 'Add Follower', headercss: { 'background-color': defaultThemeColor1 }})
        } else {
          let myMsg = `<b>Unable to add follwer:</b> <b>${mName}</b> is already a follower of <b>${fName}</b>!!!`
          msgbox({msg: myMsg,  title: 'Error: Unable to add Follower', headercss: { 'background-color': defaultThemeColor1 }})
        }
      } else {
        // Yes - there is a different master defined, so lets just move it
        let oldMaster = getObj('graphic', item.mTokenid).get('name');
        delFollower(item.mTokenid, item.sTokenid)
        fRec = {'pageid':pageid, 'mTokenid':mTokenid, 'sTokenid':sTokenid, 'leftOffset': leftOffset, 'topOffset': topOffset}
        state.myTokenEvents.followData.push(fRec);

        let myMsg = `<b>${fName}</b> has been moved from <b>${oldMaster}</b> to <b>${mName}</b>.`
        msgbox({msg: myMsg, title: 'Add Follower', headercss: { 'background-color': defaultThemeColor1 }})
      }
    } else {
      // Yes - update the associated informtion
      delFollower(mTokenid, sTokenid)
      fRec = {'pageid': pageid, 'mTokenid': mTokenid, 'sTokenid':sTokenid, 'leftOffset': leftOffset, 'topOffset': topOffset}
     // log(`  Follower already exists, updating offsets`)      
      state.myTokenEvents.followData.push(fRec);

      let myMsg = `Offset positions have been modified for <b>${fName}</b>. New offsets for left is <b>${leftOffset}</b> and top is <b>${topOffset}</b>.`
      msgbox({msg: myMsg, title: 'Update Follower', headercss: { 'background-color': defaultThemeColor1 }})
    }
    moveFollower(mTokenid, sTokenid)
  }

  function delFollower(mTokenid, sTokenid){
    let pageid = Campaign().get("playerpageid")
   // log(`delFollower: ${mTokenid} ${sTokenid} count: ${state.myTokenEvents.followData.length}`)
    // followDataNew = state.myTokenEvents.followData.filter(item => item.pageid != pageid && item.mTokenid != mTokenid && item.sTokenid != sTokenid)
    let fdNew = []
    for (let i = 0; i<state.myTokenEvents.followData.length; i++) {
      let item = state.myTokenEvents.followData[i]
      if (item.pageid === pageid && item.mTokenid === mTokenid && item.sTokenid === sTokenid){
        // Filter this record out
      } else {
        fdNew.push(item)
      }
    }
   // log(`delFollower: Remaining count: ${fdNew.length}`)
    state.myTokenEvents.followData = fdNew;
  }
  function getFollower(mTokenid, sTokenid){
    let pageid = Campaign().get("playerpageid")
   // log(`getFollower: pageid: ${pageid} mTokenid ${mTokenid} sTokenid ${sTokenid}`)  // Why can't I get this item?
    
    // let f =  followData.filter(item => item.pageid === pageid && item.mTokenid === mTokenid && item.sTokenid === sTokenid)
    for (let i = 0; i < state.myTokenEvents.followData.length; i++) {
      if (state.myTokenEvents.followData[i].pageid == pageid && state.myTokenEvents.followData[i].mTokenid == mTokenid && state.myTokenEvents.followData[i].sTokenid == sTokenid){
       // log(`getFollower found:`)  // Why can't I get this item?
        let f = state.myTokenEvents.followData[i];
        return f;
      }
    }
   // log(`getFollower Not found: mTokenid ${mTokenid} sTokenid ${sTokenid}`)  // Why can't I get this item?
  }
  function getFollowers(mTokenid){
    // Returns a list of followers for a selected tokena and page
    let pageid = Campaign().get("playerpageid")
    let f=[];
    // let f = followData.filter(item => item.pageid === pageid && item.mTokenid === mTokenid)
    state.myTokenEvents.followData.forEach(item =>{
      if (item.pageid == pageid && item.mTokenid == mTokenid){
        f.push(item);
      }
    })
   // log(`getFollowers: Count: ${f.length}`)
    return f
  }
  function getMaster(sTokenid){
    // Returns a list of followers for a selected tokena and page
    let pageid = Campaign().get("playerpageid")
    for (let i = 0; i < state.myTokenEvents.followData.length; i++) {
      let item = state.myTokenEvents.followData[i]
      if (item.pageid === pageid && item.sTokenid === sTokenid) {
        return item;
      }
    }
  }

  function moveFollower(mTokenid, sTokenid){
   // log(`moveFollower: mTokenid: ${mTokenid} sTokenid: ${sTokenid}`)
    let item = getFollower(mTokenid, sTokenid)
    if (item === undefined) return;
    dumpObject(item);
    
    let mToken = getObj('graphic', mTokenid)
    let sToken = getObj('graphic', sTokenid)

    if ((mToken) || (sToken)){
      let newLeft = Number(item.leftOffset) + Number(mToken.get('left'))
      let newTop = Number(item.topOffset) + Number(mToken.get('top'))
     // log(`${item.leftOffset} ${item.topOffset} ${mToken.get('left')} ${mToken.get('top')}`)
     // log(`Move follower to ${newLeft} and ${newTop}`)

      sToken.set('top', newTop)
      sToken.set('left', newLeft)

    } else {
     // log(`MoveFolloer: Master${mTokenid} or Shaddow${sTokenid} token not found`)
    }

    // Move the follower's followers
    let followers = getFollowers(sTokenid);
    followers.forEach(f => {
      moveFollower(sTokenid, f.sTokenid)
    });
  }

  function moveMaster(mTokenid, sTokenid){
   // log(`moveMaster: mTokenid: ${mTokenid} sTokenid: ${sTokenid}`)

    let item = getFollower(mTokenid, sTokenid)
    if (item === undefined) return
    let mToken = getObj('graphic', mTokenid)
    let sToken = getObj('graphic', sTokenid)

    if ((mToken) || (sToken)){
     // log(`MoveMaseter: Master${mTokenid}`)
      let newLeft =  (Number(sToken.get('left')) - Number(item.leftOffset))
      let newTop =  (Number(sToken.get('top')) - Number(item.topOffset))

      mToken.set('left', newLeft)
      mToken.set('top', newTop)

    } else {
     // log(`moveMaster: Master${mTokenid} or Shaddow${sTokenid} token not found`)
    }
  }

  function buildDMToolsDialog(){
    let myMsg = ''
    let tbl = ''
    let msgHandouts = '';
    let api = ''
    let btn = ''
    myMsg+= html.p('A collection of common commands/tools I use or have built', {'font-size': '11px'})

    tbl = html.tr(html.th("Run") + html.th("Description"))

    api = `!DMDash --open`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard'))

    api = `!DMDash --help`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard Help'))

    api = `!DMDash --config`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard Config'))

    api = `!DMDash --clearcache`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard Clear Cache'))

    api = `!DMDash --track`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Jukebox'))

    api = `!DMDash --ssm`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Status Markers'))

    api = `!DMnotes --build`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Notes'))

    api = `!DMDash --playerhandout`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Player Handout'))

    api = `!DMDash --playerhandout`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Player Handout'))

    api = `!CAMPAIGNHEALTH`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Campaign Database Health'))

    // Health Check
    api = `!HEALTHCHECK`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Update health indicators for all tokens on current page'))

    // Followers
    api = `!Follow --show`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Show followers on current page'))


    tbl = html.table(tbl)

    myMsg+=tbl

    msgbox({msg: myMsg, 
          title: 'DM Tools',
          whisperto: `GM`,          
          headercss: { 'background-color': defaultThemeColor1 }})
  }

  function showFollowers(){
    let myMsg = ''
    let tbl = ''

    myMsg+= html.p('Lists followers for current page', {'font-size': '11px'})

    // List Followers
    tbl = html.tr(html.th("Master") + html.th("Follower"))

    state.myTokenEvents.followData.forEach(item => {
      let mName = getObj('graphic', item.mTokenid).get('name');
      let fName = getObj('graphic', item.sTokenid).get('name');
      tbl+=html.tr(html.td(mName)+html.td(fName))
    })

    tbl = html.table(tbl)
    myMsg+=tbl

    let api = `!Follow --clear`;
    let btn = btnAPI({api: api, label: `Clear All Data`});

    msgbox({msg: myMsg, 
          title: 'Show Followers',
          btn: btn,
          whisperto: `GM`,
          headercss: { 'background-color': defaultThemeColor1 }})
  }

  function buildUserDialog(selTokenId){   // Create a clean looking dialog with the following features
    // Allows a player to:
    //   Select a follower for the selected token 
    //   Remove a follower for the selected token
    //   Lists current followers and their current offsets
    //   Edit/Adj offsets for each follower
    let myMsg='';
    let selToken = getObj('graphic', selTokenId);
    let selTokenName = ''
    if (selToken) {
      selTokenName = selToken.get('name');
    }
   // log(`buildUserDialog: ${selTokenId} / ${selTokenName}`)

    myMsg=html.h2(selTokenName)
    myMsg+= html.p('From this dialog, you can add, edit and remove followers')

    // List Followers
    let followers = getFollowers(selTokenId)
    if (followers.length > 0) {
      myMsg+= html.h3('My Followers:')
      followers.forEach( f=> {
        let fName = getObj('graphic', f.sTokenid).get('name');
        let a = `!Follow --delete ${selTokenId} ${f.sTokenid}`;
        let bDel = btnAPI({api: a, label: `Del`},{'border-radius': '1px', 'padding': '1px 1px'});
        a = `!Follow --edit ${selTokenId} ${f.sTokenid} ?{Left Offset|${f.leftOffset}} ?{Top Offset|${f.topOffset}}`;
        let bEdit = btnAPI({api: a, label: `Edit`},{'border-radius': '1px', 'padding': '1px 1px'});
        myMsg+= html.p(` ${bDel} ${bEdit} ${fName} offsets: ${f.leftOffset}, ${f.topOffset}`, {"font-size": "10px"})
      })
    }

    // List Masters
    let master = getMaster(selTokenId)
    if (master != undefined) {
      myMsg+= html.h3('My Master:')
      let mName = getObj('graphic', master.mTokenid).get('name');
      myMsg+= html.p(` - ${mName} offsets: ${master.leftOffset}, ${master.topOffset}`)
    }

    let api = `!Follow --Add @{selected|token_id} @{target|token_id}`;
    let btn = btnAPI({api: api, label: `Add Follower`});
    
    msgbox({msg: myMsg, 
          title: 'Manage Followers',
          btn: btn, 
          headercss: { 'background-color': defaultThemeColor1 }})
   
  }

  function setHealthIndicatorsForPage(){
    let pageid = Campaign().get("playerpageid")
    let tokens = findObjs({
      _type: 'graphic',
      _pageid: pageid,
      _subtype: 'token'
    })
    log(`Setting health indicators Count: ${tokens.length}`)
    tokens.forEach(token => {
      if (token.get("bar1_max") !== undefined) {
        if (token.get("bar1_max") > 0) {
          setHealthIndicator(token)
        }
      }
    });
  }

  function setHealthIndicator(token){

    //Bar 1 should contain the hp - calculate health pct
    let hp = Number(token.get('bar1_value'))
    let hp_max = Number(token.get('bar1_max'))
    hpPct = hp / hp_max * 100; 
    //token.set('bar3_value', hpPct);
    log(`SetHealthIndicator: ${token.get('name')} pct: ${hpPct} bar1_max: ${hp_max} bar1_value: ${hp}`)

    removeHealthMarker(token); 

    if (hpPct >75 && hpPct <= 95) {
      setHealthMarker(token, smH1); return
    } else if (hpPct >50 && hpPct <= 75) {
      setHealthMarker(token, smH2); return
    } else if (hpPct >25 && hpPct <= 50) {
      setHealthMarker(token, smH3); return
    } else if (hpPct >0 && hpPct <= 25) {
      setHealthMarker(token, smH4); return
    } else if (hpPct <=0) {
      setHealthMarker(token, smDead); return
    }
  }
  function removeHealthMarker(token){
    let markers = token.get('statusmarkers');
    let newMarkers = ''
    
    if (markers.indexOf(smH1) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH1).join(',');
      token.set('statusmarkers', newMarkers);
    }    
    if (markers.indexOf(smH2) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH2).join(',');
      token.set('statusmarkers', newMarkers);
    }    
    if (markers.indexOf(smH3) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH3).join(',');
      token.set('statusmarkers', newMarkers);
    }    
    if (markers.indexOf(smH4) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH4).join(',');
      token.set('statusmarkers', newMarkers);
    }
    if (markers.indexOf(smDead) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smDead).join(',');
      token.set('statusmarkers', newMarkers);
    }
  }

  function setHealthMarker(token, marker){
    let markers = token.get('statusmarkers');
    let newMarkers = '';
    log (`SetHealthMarker ${token.get('name')} ${marker}`);
    newMarkers = markers.split(',');
    newMarkers.push(marker);
    newMarkers = newMarkers.join(',');
    log (`SetHealthMarker ${newMarkers}`);
    token.set('statusmarkers', newMarkers);
  }

  function dumpObject(obj){
    let output = ''
    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        output+= `  Field Name: ${key} => ${obj[key]}<br>`
        log (`  Field Name: ${key} => ${obj[key]}`)
      }
    }
    return output;
  }

  function dumpFollowData(){
    let output = ''
    let i = 0

    log (`Dumping followData: Array Count is ${state.myTokenEvents.followData.length}`)
    state.myTokenEvents.followData.forEach(item => {
     // log(`FollowData[${i}]: pageid: ${item.pageid} mTokenid: ${item.mTokenid} sTokenid: ${item.sTokenid} topOffset: ${item.topOffset} leftOffset: ${item.leftOffset}`)
      i++;
    })

  }

  function addTextToHandout(noteTxt, handoutName, mode){
    // mode - Determines how txt will be added to the handout
    //  0: Complete replacement
    //  1: Append to bottom
    //  2: Push to the top
    //  3: Append to the bottom and include a timestamp

    const ho = getHandout(handoutName);
    let newText = '';

    // Add the text to the note 
    ho.get("notes", function(notes) {
      switch(mode){
      case 0:
      case '0':
        newText = noteTxt;
        break;
      case 1:
      case '1':
        newText = `${notes}<br>${noteTxt}`
        //myDebug(4, `addTextToHandout: Mode 1 (${handoutName}, mode:${mode}, len:(${newText.length})`)        
        break;
      case 2:
      case '2':
        //myDebug(4, `addTextToHandout: Mode 2 (${handoutName}, mode:${mode}, len:(${newText.length})`)
        newText = `${noteTxt}<br>${notes}`
        break;
      case 3:
      case '3':
        //myDebug(4, `addTextToHandout: Mode 3 (${handoutName}, mode:${mode}, len:(${newText.length})`)
        let sysDate = GetSystemUTCDate();
        newText = `${notes}<br>${sysDate}: ${noteTxt}`
        break;
      }
      //myDebug(4, `addTextToHandout: set (${handoutName}, mode:${mode}, len:(${newText.length})`)
      setTimeout(()=>ho.set("notes", newText),0);
    });
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

  function myDebug(lvl,txt){
    // giDebug: 0 - Off, 1 - API Log, 2 - Chat, 3 - Chat & Log, 4 - Handout (future)
    // giDebugLvl: 0 - All, 1 - Low Info, 2 - High Info, 3 - Basic Debug, 4 - New Code Debug, 5 - Performance logging
  
    if ((gDebug == 1 || gDebug == 3) && lvl >= gDebugLvl) {
     // log(txt);
    }
    if ((gDebug == 2 || gDebug == 3) && lvl >= gDebugLvl) {
      sendChat('Debug','/w gm '+txt);
    }
    if (gDebug == 4 && lvl >= gDebugLvl) {
      // Add to Handout
      addTextToHandout(`${txt} lvl(${lvl}`, "DM Debug Log", 3) 
    }
  }
  function mySendChat(whisper, title, message, links){
    const openChat = `<div style="border-radius: 10px ; border: none ; background-color: ; overflow: hidden ; width: 100%"><div style = "border-radius: 10px ; border: 2px solid #000000 ; background-color:  #00000000; overflow: hidden ; margin: 0px 16px 16px 0px ; box-shadow: 5px 8px 8px #888888">`
    const closeChat= `<\div><\div>`;
    const tblChatStyle = `width: 100% ; margin: 0 auto ; border-collapse: collapse ; font-size: 12px;`
    const trhChatStyle = `border-bottom: 1px solid #000000 ; font-weight: bold ; line-height: 22px ; background-color: #521e10 ; color: #ffffff;`
    const tdChatStyle = `padding: 4px ; min-width: 10px;`
    
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
    sendChat('DMDashboard', chatTxt);
  }

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
  const defaultpCSS = {"font-size": "10px"};
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
      'min-width': '5px',
      'padding': '3px 4px'
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
      title: title = "DMDash Output",
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

});
{try{throw new Error('');}catch(e){API_Meta.myTokenEvents.lineCount=(parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/,'$1'),10)-API_Meta.myTokenEvents.offset);}}