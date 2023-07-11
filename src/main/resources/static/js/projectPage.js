$(document).ready(function() {
    var $sidebar = $('#sidebar');
    var $mainContent = $('#main-content');

    $('#toggle-sidebar').click(function() {
        if ($sidebar.width() === 0) { // if the sidebar is hidden
            $sidebar.animate({ width: '200px' }, 500); // expand the sidebar
            $mainContent.animate({ 'margin-left': '200px' }, 500); // and move the main content to the right
        } else { // if the sidebar is visible
            $sidebar.animate({ width: '0' }, 500); // hide the sidebar
            $mainContent.animate({ 'margin-left': '0' }, 500); // and move the main content back to the left
        }
    });
});

