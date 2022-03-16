class UserForm {
    constructor(formHTMLElement, rolesArray) {
        this.inputUserPassword = formHTMLElement.querySelector(".user-password");
        this.inputUserSurname = formHTMLElement.querySelector(".user-surname");
        this.inputUserLogin = formHTMLElement.querySelector(".user-login");
        this.inputUserRoles = formHTMLElement.querySelector(".user-roles");
        this.inputUserName = formHTMLElement.querySelector(".user-name");
        this.inputUserId = formHTMLElement.querySelector(".user-id");
        
        this.HTMLEntity = formHTMLElement;
        this.rolesArray = rolesArray;
    }

    clearForm() {
        this.HTMLEntity.querySelectorAll("input").forEach(e => e.value = "");
        this.inputUserId.value = -1;
    }

    setUserDataFromUserObj (user) {
        this.inputUserLogin.value = user.login;
        this.inputUserName.value = user.name;
        this.inputUserSurname.value = user.surname;
        
        this.inputUserRoles.innerHTML = user.rolesList.reduce( (result, role) => {
            return result += `<option ${role === user.role ? "selected" : ""}>${role}</option>`
        }, "");
    }

    setUserId(userId) {
        this.inputUserId.value = userId;
    }
}

class UserModal {
    constructor(modalHTMLElement) {
        this.HTMLEntity = modalHTMLElement;
        this.title = modalHTMLElement.querySelector(".modal-title");
        this.form = new UserForm(
            modalHTMLElement.querySelector(".modal-form"),
            [ "Admin", "User" ] //loadUsersRolesList() // TODO: rewrite
        );
    }

    reset() {
        this.form.clearForm();
    }
}

class User {
    constructor({ userId, firstName, lastName, login, role: { rolesList, currentRole} }) {
        this.userId = userId;
        this.name = firstName;
        this.surname = lastName;
        this.login = login;
        this.role = currentRole;
        this.rolesList = rolesList;
    }
}



const modalCreateUser = new UserModal( document.querySelector("#modal-create-user") );
const modalEditUser = new UserModal( document.querySelector("#modal-edit-user") );
const usersList = document.querySelector(".users__list");

const modalDeleteUser = document.querySelector("#modal-delete-user");
const formDeleteUser = modalDeleteUser.querySelector("#form-delete-user");

usersList
    .querySelectorAll(".user-info__button_delete")
    .forEach(btn => btn.addEventListener("click", async e => {
        formDeleteUser
            .querySelector(".user-id")
            .value = e.target
                .closest(".user-info")
                .dataset
                .id;
    }));

usersList
    .querySelectorAll(".user-info__button_edit")
    .forEach(btn => btn.addEventListener("click", async e => {
        const userId = e.target.closest(".user-info").dataset.id;
        const result = await loadUserById(userId);

        modalEditUser.form.setUserId(userId);
        modalEditUser.form.setUserDataFromUserObj(result);
    }));



async function loadUserById(userId) {
    const url = new URL("http://localhost:8080/admin/editUser");
    url.search = new URLSearchParams({ "id" : userId }).toString();

    const response = await fetch(url);

    if (response.ok) {
        return new User(await response.json());
    }
}

// ATTENTION
// This method is just placeholder until new controller method appear.
// New controller method at the back-end have to return list of user roles in JSON format.
// TODO: Update this one as soon as possible
async function loadUsersRolesList() {
    return Array.from(modalEditUser.form.inputUserRoles.querySelectorAll("option"))
        .map(opt => opt.value);
}
