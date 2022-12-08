
var showeditForm = false;
const edit_button = document.getElementById("edit");
const edit_form = document.getElementById("show_edit_form");
const view_profile = document.getElementById("show_profile");

function switching(){
    if(!showeditForm){ // view -> edit
        showeditForm = true;
        edit_button.innerHTML = 'View Profile';
        edit_form.hidden = false;
        view_profile.hidden = true;
    }
    else {
        showeditForm = false;
        edit_button.innerHTML = 'Edit Profile';
        edit_form.hidden = true;
        view_profile.hidden = false;
    }

}

