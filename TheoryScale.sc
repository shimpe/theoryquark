TheoryScale {
	var <>referencenote;
	var <>name;
	var <>notes;
	var <>midi_to_degree;

	classvar >parser;
	classvar >steps_per_octave;

	*new {
		| basenote, type, collection|
		^super.new.init(basenote, type, collection);
	}

	*initClass {
		Class.initClassTree(TheoryNoteParser);
		this.parser = TheoryNoteParser.new;
		this.steps_per_octave = 12;
	}

	init {
		| basenote, type, collection |
		var m;
		var reduction;
		referencenote = basenote;
		name = type;
		notes = collection;
		midi_to_degree = Dictionary.new;
		m = this.asMidi.value;
		reduction = ((m.minItem).div(12))*12;
		m.do({
			| midi, idx |
			midi_to_degree[midi - reduction ] = idx;
		});
	}

	printOn {
		arg stream;
		stream << "TheoryScale(\"" << referencenote << "\", \"" << name << "\"" << ", \"" << notes << "\")";
	}

	asMidi {
		if (notes.isKindOf(String), { ^parser.asMidi(notes) });
		if (notes[0].isKindOf(SimpleNumber),{ ^notes });
		^notes.collect({ | note | parser.asMidi(note); })[0];
	}

	asDegree {
		^midi_to_degree.collect({ | el, i | el[i] });
	}

	noteToDegree {
		| note |
		var midi = parser.asMidi(note)[0];
		^this.midiToDegree(midi);
	}

	midiToDegree {
		| midi |
		var reduced_midi = midi.mod(steps_per_octave);
		var min_midi = midi_to_degree.keys().minItem;
		var max_midi = midi_to_degree.keys().maxItem;
		while ({reduced_midi < min_midi}, { reduced_midi = reduced_midi + steps_per_octave; });
		if ((reduced_midi < max_midi), {
			var ub = 10000;
			var lb = 0;
			midi_to_degree.keys.do({
				| midi |
				if (((midi > lb) && (midi <= reduced_midi)), { lb = midi; });
				if (((midi < ub) && (midi >= reduced_midi)), { ub = midi; });
			});
			^reduced_midi.linlin(lb, ub, midi_to_degree[lb], midi_to_degree[ub]);
		}, {
			var next_midi = min_midi + steps_per_octave;
			var next_degree = midi_to_degree[max_midi] + 1;
			^reduced_midi.linlin(max_midi, next_midi, midi_to_degree[max_midi], next_degree);
		});
	}

	degreeToMidi {
		| degree, octave |
		var degree_to_midi = midi_to_degree.invert;
		var min_degree = 0;
		var max_degree = degree_to_midi.keys.maxItem;
		var no_of_degrees = degree_to_midi.size;
		var reduced_degree = degree.mod(no_of_degrees);
		var extra_octaves = degree.div(no_of_degrees);
		var ub = 10000;
		var lb = 0;
		if ((reduced_degree < max_degree), {
			degree_to_midi.keys.do({
				| deg |
				if (((deg > lb) && (deg <= reduced_degree)), { lb = deg; });
				if (((deg < ub) && (deg >= reduced_degree)), { ub = deg; });
			});
			^((reduced_degree.linlin(lb, ub, degree_to_midi[lb], degree_to_midi[ub])) + ((octave+extra_octaves+1)*steps_per_octave));
		}, {
			var next_degree = min_degree + no_of_degrees;
			var next_midi = degree_to_midi[max_degree]+1;
			^reduced_degree.linlin(max_degree, next_degree, degree_to_midi[max_degree], next_midi);
		});
	}
}