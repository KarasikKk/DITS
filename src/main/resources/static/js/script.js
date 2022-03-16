/**
 * TODO:
 * - Rename this file
 * 
 * - Put all logic into one method,
 * that passed in eventListener
 * 
 */

const dataContainer = document.getElementById('dataContainer');
let isReverseTest = false;


function reverseTests(target) {
  target.closest('#sortTestsButton').classList.toggle('reverse');
  const reversedTests = Array.from(resultTableBody.querySelectorAll('.test')).reverse();
  resultTableBody.innerHTML = '';
  reversedTests.forEach( test => {
    resultTableBody.append(test);
  })
}


dataContainer.addEventListener('click', (event) => {
  const { target } = event;
  if(target.closest('#sortTestsButton')) {
    isReverseTest = !isReverseTest;
    reverseTests(target);
  } 
})
  