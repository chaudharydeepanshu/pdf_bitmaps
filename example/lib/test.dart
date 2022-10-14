import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:pdf_bitmaps/pdf_bitmaps.dart';
import 'package:pick_or_save/pick_or_save.dart';

class MyTestApp extends StatefulWidget {
  const MyTestApp({Key? key}) : super(key: key);

  @override
  State<MyTestApp> createState() => _MyTestAppState();
}

class _MyTestAppState extends State<MyTestApp> {
  final _pickOrSavePlugin = PickOrSave();

  final bool _localOnly = false;
  final bool _copyFileToCacheDir = false;
  List<String>? _pickedFilePath;
  List<Uint8List> pageBytesList = [];

  int pdfPageCount = 0;
  final _pdfBitmapsPlugin = PdfBitmaps();

  @override
  void initState() {
    super.initState();
  }

  Future<void> _filePicker(FilePickerParams params) async {
    List<String>? result;
    try {
      result = await _pickOrSavePlugin.filePicker(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _pickedFilePath = result;
    });
  }

  Future<void> getPDFPageCount({required PDFPageCountParams? params}) async {
    int pageCount;
    try {
      pageCount = await _pdfBitmapsPlugin.pdfPageCount(params: params) ?? 0;
    } on PlatformException {
      pageCount = 0;
    }
    if (!mounted) return;

    setState(() {
      pdfPageCount = pageCount;
    });
  }

  Future<void> getPDFPageBitmaps({required PDFBitmapsParams? params}) async {
    List<Uint8List>? bytesList;
    try {
      bytesList = await _pdfBitmapsPlugin.pdfBitmaps(params: params);
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;

    setState(() {
      pageBytesList = bytesList ?? [];
    });
  }

  Future<void> _cancelTask() async {
    String? result;
    try {
      result = await _pdfBitmapsPlugin.cancelBitmaps();
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              OutlinedButton(
                  onPressed: () async {
                    final params = FilePickerParams(
                      localOnly: _localOnly,
                      copyFileToCacheDir: _copyFileToCacheDir,
                    );
                    await _filePicker(params);
                  },
                  child: const Text('pick pdf')),
              OutlinedButton(
                  onPressed: () {
                    final params = PDFPageCountParams(
                      pdfPath: _pickedFilePath![0],
                    );
                    getPDFPageCount(params: params);
                  },
                  child: const Text('Get page count of selected pdf')),
              OutlinedButton(
                  onPressed: () async {
                    final params = PDFBitmapsParams(
                      pdfPath: _pickedFilePath![0],
                      pagesInfo: [
                        PageInfo(pageNumber: 1, scale: 5),
                        PageInfo(pageNumber: 2, scale: 5),
                        PageInfo(pageNumber: 3, scale: 5),
                        PageInfo(pageNumber: 4, scale: 5)
                      ],
                    );

                    await getPDFPageBitmaps(params: params);
                  },
                  child: const Text('get bitmaps')),
              OutlinedButton(
                  onPressed: () async {
                    await _cancelTask();
                  },
                  child: const Text("Cancel bitmaps tasks")),
              Expanded(
                child: ListView.builder(
                  itemCount: pageBytesList.length,
                  itemBuilder: (BuildContext context, int index) {
                    return Image.memory(
                      pageBytesList[index],
                      // width: 100,
                      // height: 100,
                      fit: BoxFit.contain,
                    );
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
