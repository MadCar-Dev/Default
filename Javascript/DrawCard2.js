const drawCard = (deckName, cardName, playerID) => {
  const deck = findObjs({ type: "deck", name: deckName })[0];

  log('DrawCard Params deck, card, pid' + deckName + ' / ' + cardName + ' / ' + playerID);

  if (!deck) {
    sendChat("System", `Deck with name "${deckName}" not found.`);
    return;
  }
  const cards = findObjs({ type: "card", deckid: deck.id });
  const card = cards.find((c) => c.get("name") === cardName);

  if (!card) {
    sendChat("System", `Card with name "${cardName}" not found in the deck "${deckName}".`);
    return;
  }
  let cardID = card.get('id');

  log('IDs (D,C,P)' + deck.get('_id') + ' / ' + cardID + ' / ' + playerID)

  giveCardToPlayer(card.get('_id'), playerID);

  return;
}

const takeCard = (deckName, cardName, playerID) => {
  const deck = findObjs({ type: "deck", name: deckName })[0];

  log('TakeCard Params deck, card, pid' + deckName + ' / ' + cardName + ' / ' + playerID);

  if (!deck) {
    sendChat("System", `Deck with name "${deckName}" not found.`);
    return;
  }
  const cards = findObjs({ type: "card", deckid: deck.id });
  const card = cards.find((c) => c.get("name") === cardName);

  if (!card) {
    sendChat("System", `Card with name "${cardName}" not found in the deck "${deckName}".`);
    return;
  }
  let cardID = card.get('id');

  log('IDs (D,C,P)' + deck.get('_id') + ' / ' + cardID + ' / ' + playerID)

  takeCardFromPlayer(playerID, card.get('_id'));

  return;
}


on("chat:message", (message) => {
  if (message.type === "api" && (message.content.startsWith("!DrawCard") || message.content.startsWith("!TakeCard")) ) {
    const args = message.content.split(" ").slice(1);
      if (args.length === 3) {
        if (message.content.startsWith("!DrawCard")) {
          drawCard(args[0], args[1], args[2]);
        } else {
          takeCard(args[0], args[1], args[2]);
        }
      } else {
        sendChat("System", "Usage: !DrawCard [Deck Name] [Card Name] [PlayerID]");
      }
    }
  });

on('ready', () => {
  const version = '0.0.1';
  log('DrawCard' + version + ' is ready!');

  // Check if the namespaced property exists, creating it if it doesn't
});