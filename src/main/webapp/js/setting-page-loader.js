/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

let username = null;

/**
 * Fetch the current logged in username
 */
function fetchUsername() {
    fetch('/login-status')
        .then((response) => {
            return response.json();
        })
        .then((loginStatus) => {
            if (loginStatus.isLoggedIn) {
                username = loginStatus.username;
            }
        });
}

/** Fetches the about me part and add them to the page. */
function fetchDisplayedName(){
    const url = '/setting';
    fetch(url)
        .then((response) => {
            return response.json();
        })
        .then((userSettings) => {
            const displayedNameInput = document.getElementById('displayed-name-input');
            displayedNameInput.innerHTML = userSettings.displayedName;
        });
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
    fetchUsername();
    fetchDisplayedName();
}
