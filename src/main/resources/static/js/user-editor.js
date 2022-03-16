/**
 * TODO:
 * - delete console.log for debugging
 * 
 */

const testThemes = document.getElementById('testThemes');
console.log(testThemes)

testThemes.addEventListener('click', ({ target }) => {
    const testItem = target.closest('.test-item');
    console.log(testItem)
    if (testItem) {
        testItem.querySelector('.test-item__text').setAttribute( 'contenteditable',"true");
        testItem.classList.add('edit');
    }
    
})

// TODO: change ID because the good practice is next style "activate-add-test-button"
const activateAddTestButton = document.getElementById('activateAddTestButton');
const addTestForm = document.getElementById('addTestForm');

activateAddTestButton.addEventListener('click', () => {
    addTestForm.classList.toggle('active');
})

const detailList = document.getElementById('detailList');

detailList.addEventListener('click', ({ target }) => {
    if (target.closest('.question')) {
        clickQuestionHandler(target);
    }
})

function clickQuestionHandler(target) {
    if(target.classList.contains('question__text')) {
        target.closest('.question').classList.toggle('open')
    }
}