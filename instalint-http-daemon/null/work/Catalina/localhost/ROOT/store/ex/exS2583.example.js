// Conditionally executed blocks should be reachable

function calculateOpenState() {
	let model = this;
	let INV = STATES.indexOf('Invalid');
	let UNP = STATES.indexOf('Unpublished');
	let COMP = STATES.indexOf('Completed');
	let PEND = STATES.indexOf('Pending');
	let OPEN = STATES.indexOf('Open');
	let rank;
	if (model.isPublished) {
		let today = new Date();
		let start = new Date(model.dateStarted);
		let end = new Date(model.dateCompleted);
		let isOpen = start <= today && today <= end;
		if (isOpen) {
			rank=OPEN;
		} else {
			if (today < start) {
				rank = PEND;
			} else if (today > end) {
				rank = COMP;
			} else {
				rank = INV;
			}
		}
	} else {
		rank = UNP;
	}
	return {state: STATES[rank], rank: rank };
}
