package com.bau.puzzlegame.model

//card puzzle model
class PuzzleModel {

    var id: String = ""
    var urlImage: String = ""
    var name: String = ""
    var visible: Boolean = true

    constructor(id: String, urlImage: String, name: String) {
        this.id = id
        this.urlImage = urlImage
        this.name = name
        this.visible = true
    }

    constructor() {
        this.id = ""
        this.urlImage = ""
        this.name = ""
        this.visible = true
    }


}