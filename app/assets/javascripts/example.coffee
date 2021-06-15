$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/create'
    get: '/get'
    getDocuments: '/get-documents'
    delete: '/delete'
    update: '/update'
    loginPost: '/loginPost'
    saveDocuments: '/save-new-document'
    getDocumentsBySection: '/get-documents-by-section'
    getDocumentsByDocumentType: '/get-documents-by-docType'

  defaultLogin =
    login: ''
    password: ''

  defaultDocument =
    section: ''
    documentType: ''
    subDocumentType: ''

  vm = ko.mapping.fromJS
    name: ''
    getList: []
    id: 0
    tel: ''
    age: ''
    address: ''
    login: defaultLogin
    documents: defaultDocument
    getDocumentsList: []
    filter:
      section: ''
      documentType: ''


  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.convertIntToDateTime = (intDate)->
    moment(parseInt(intDate)).format('MMM DD, YYYY HH:mm:ss')

  vm.loginPost = ->
    toastr.clear()
    if !vm.login.login()
      toastr.error("""Iltimos "Login" ni kiriting!""")
    else if !vm.login.password()
      toastr.error("""Iltimos "Password" ni kiriting!""")
    else
      defaultLogin = "admin"
      defaultPassword = "admin123"
      if vm.login.login() is defaultLogin and vm.login.password() is defaultPassword
        window.location.href = '/index'
      else
        toastr.error("Qaytadan urinib ko`ring Login yoki Password xato bo`lishi mumkin!")

  $documentModal = $('#document-save')

  vm.addDocuments = ->
    $documentModal.modal('show')

  vm.saveDocument = ->
    toastr.clear()
    console.log vm.documents.section()
    if vm.documents.section() is 'tanlang'
      toastr.error('Iltimos bo`limni tanlang!')
    else if vm.documents.documentType() is 'tanlang'
      toastr.error('Iltimos hujjat turini tanlang!')
    else if vm.documents.documentType() is 'boshqa' and !vm.documents.subDocumentType()
      toastr.error('Iltimos hujjat turini kiriting!')
    else
      data = ko.mapping.toJS vm.documents
      $.post(apiUrl.saveDocuments, JSON.stringify(data))
      .fail handleError
      .done (res) ->
        getDocuments()
        $documentModal.modal('hide')
        toastr.success(res)

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.name())
      toastr.error("Please enter a name")
      return no
    else if(!vm.tel())
      toastr.error("Please enter a tel")
      return no
    else if (!vm.age())
      toastr.error("Please enter a age")
      return no
    else if (!vm.address())
      toastr.error("Please enter a address")
      return no
    else
      data =
        name: vm.name()
        tel: vm.tel()
        age: vm.age()
        address: vm.address()
      $.ajax
        url: apiUrl.send
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)

  vm.getAllNames = ->
    $.ajax
      url: apiUrl.get
      type: 'GET'
    .fail handleError
    .done (response) ->
      console.log('1: ', vm.getList().length)
      vm.getList(response)
      console.log('2: ', vm.getList().length)

  getDocuments = ->
    $.get(apiUrl.getDocuments)
    .fail handleError
    .done (response) ->
      vm.getDocumentsList(response)
  getDocuments()

  vm.getDocumentsBySection = (section) ->
    $.get(apiUrl.getDocumentsBySection + "/#{section}")
    .fail handleError
    .done (response) ->
      vm.getDocumentsList(response)

  vm.getDocumentsByDocumentType = (docType) ->
    $.get(apiUrl.getDocumentsByDocumentType + "/#{docType}")
    .fail handleError
    .done (response) ->
      vm.getDocumentsList(response)

  vm.filter.section.subscribe (value) ->
    if value is 'all'
      getDocuments()
    else
      vm.getDocumentsBySection(value)

  vm.filter.documentType.subscribe (value) ->
    if value is 'all'
      getDocuments()
    else
      vm.getDocumentsByDocumentType(value)

  vm.getSectionName = (name) ->
    if name is 'teacher'
      'Professor-O`qtuvchilar'
    else if name is 'scientificWorks'
      'Ilmiy ishlar'
    else if name is 'student'
      'Talabalar'
    else if name is 'akt'
      'AKT'
    else if name is 'kafedralar'
      'Kafedralar'
    else if name is 'bugalteriya'
      'Bugalteriya'
    else
      ''

  vm.getDocumentsName = (name) ->
    if name is 'lab'
      'Labaratoriya'
    else if name is 'practice'
      'Amaliyot'
    else if name is 'independent'
      'Mustaqil ish'
    else if name is 'induvidual'
      'Induvidual loyiha'
    else if name is 'bmi'
      'BMI'
    else
      ''

  vm.delete = ->
    if window.confirm(" Do you want to delete it?" )
      data =
        id: parseInt(vm.id())
      $.ajax
        url: apiUrl.delete
        type: 'DELETE'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)

  vm.update = ->
    data =
      id: parseInt(vm.id())
      name: vm.name()
      tel: vm.tel()
      age: vm.age()
      address: vm.address()
    $.ajax
      url: apiUrl.update
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)


  ko.applyBindings {vm}