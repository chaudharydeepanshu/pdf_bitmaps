import 'dart:async';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:pdf_bitmaps/pdf_bitmaps.dart';
import 'package:pick_or_save/pick_or_save.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'PDF bitmaps example',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(useMaterial3: true),
      darkTheme: ThemeData.dark(useMaterial3: true),
      themeMode: ThemeMode.system,
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        ScaffoldMessenger.of(context).hideCurrentSnackBar();
      },
      child: DefaultTabController(
        length: 3,
        child: Scaffold(
          appBar: AppBar(
            bottom: TabBar(
              tabs: [
                Text("GridView Example",
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.labelSmall),
                Text("Protection & Validity Example",
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.labelSmall),
                Text("Page Size Info Example",
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.labelSmall),
              ],
            ),
            title: const Text('PDF bitmaps example'),
          ),
          body: const TabBarView(
            children: [
              LoadingPagesInGridView(),
              PdfProtectionAndValidityInfo(),
              GetPageSizeInfo(),
            ],
          ),
        ),
      ),
    );
  }
}

class LoadingPagesInGridView extends StatefulWidget {
  const LoadingPagesInGridView({Key? key}) : super(key: key);

  @override
  State<LoadingPagesInGridView> createState() => _LoadingPagesInGridViewState();
}

class _LoadingPagesInGridViewState extends State<LoadingPagesInGridView> {
  final _pickOrSavePlugin = PickOrSave();
  final _pdfBitmapsPlugin = PdfBitmaps();

  bool _isBusy = false;
  String? _pickedPDFPath;
  List<Map<String, dynamic>>? listOfBytesAndIndex;
  bool rejectByteUpdate = false;

  int bitmapRotationAngle = 0;
  double bitmapScale = 1;
  Color bitmapBackgroundColor = Colors.white;
  PdfRendererType pdfRendererType = PdfRendererType.androidPdfRenderer;

  Future<List<String>?> _filePicker(FilePickerParams params) async {
    List<String>? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _pickOrSavePlugin.filePicker(params: params);
    } on PlatformException catch (e) {
      log(e.toString());
    } catch (e) {
      log(e.toString());
    }
    if (!mounted) return result;
    setState(() {
      _isBusy = false;
    });
    return result;
  }

  Future<int?> getPDFPageCount({required PDFPageCountParams? params}) async {
    int? pageCount;
    try {
      pageCount = await _pdfBitmapsPlugin.pdfPageCount(params: params) ?? 0;
    } on PlatformException catch (e) {
      log(e.toString());
    } catch (e) {
      log(e.toString());
    }
    return pageCount;
  }

  void updateBytesIntoList(int index) async {
    if (listOfBytesAndIndex![index]["bytes"] == null &&
        rejectByteUpdate != true) {
      rejectByteUpdate = true;
      String? imageFilePath = await _pdfBitmapsPlugin.pdfBitmap(
        params: PDFBitmapParams(
          pdfPath: _pickedPDFPath!,
          pageInfo: BitmapConfigForPage(
            pageNumber: index + 1,
            rotationAngle: bitmapRotationAngle,
            scale: bitmapScale,
            backgroundColor: bitmapBackgroundColor,
          ),
          pdfRendererType: pdfRendererType,
        ),
      );
      Uint8List? bytes = File(imageFilePath!).readAsBytesSync();
      setState(() {
        listOfBytesAndIndex![index]["bytes"] = bytes;
        rejectByteUpdate = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8.0),
      child: Column(
        children: [
          CustomButton(
              buttonText: 'Pick Single PDF',
              onPressed: _isBusy
                  ? null
                  : () async {
                      final params = FilePickerParams(
                          localOnly: false,
                          mimeTypesFilter: ["application/pdf"],
                          allowedExtensions: [".pdf"]);

                      List<String>? result = await _filePicker(params);

                      if (mounted) {
                        callSnackBar(context: context, text: result.toString());
                      }

                      if (result != null && result.isNotEmpty) {
                        setState(() {
                          _pickedPDFPath = result[0];
                        });

                        int? pdfPageCount = await getPDFPageCount(
                            params: PDFPageCountParams(
                          pdfPath: _pickedPDFPath!,
                        ));

                        if (pdfPageCount != null) {
                          setState(() {
                            listOfBytesAndIndex =
                                List<Map<String, dynamic>>.generate(
                                    pdfPageCount,
                                    (int index) =>
                                        {"index": index, "bytes": null},
                                    growable: true);
                          });
                        } else {
                          log("Page count is null");
                        }
                      }
                    }),
          const SizedBox(height: 8),
          Text(
              "Bitmaps Config:\nRotationAngle - $bitmapRotationAngle, Scale - $bitmapScale, BackgroundColor - $bitmapBackgroundColor, PdfRendererType - $pdfRendererType",
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.labelSmall),
          const SizedBox(height: 8),
          listOfBytesAndIndex != null && _pickedPDFPath != null
              ? Expanded(
                  child: GridView.builder(
                      gridDelegate:
                          const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 3,
                        mainAxisSpacing: 10,
                      ),
                      itemCount: listOfBytesAndIndex!.length,
                      itemBuilder: (BuildContext context, int index) {
                        updateBytesIntoList(index);
                        return listOfBytesAndIndex![index]["bytes"] != null
                            ? Image.memory(
                                listOfBytesAndIndex![index]["bytes"]!,
                                // width: 100,
                                // height: 100,
                                fit: BoxFit.contain,
                              )
                            : const Center(child: CircularProgressIndicator());
                      }),
                )
              : const SizedBox(),
        ],
      ),
    );
  }
}

class PdfProtectionAndValidityInfo extends StatefulWidget {
  const PdfProtectionAndValidityInfo({Key? key}) : super(key: key);

  @override
  State<PdfProtectionAndValidityInfo> createState() =>
      _PdfProtectionAndValidityInfoState();
}

class _PdfProtectionAndValidityInfoState
    extends State<PdfProtectionAndValidityInfo> {
  final _pickOrSavePlugin = PickOrSave();
  final _pdfBitmapsPlugin = PdfBitmaps();

  bool _isBusy = false;
  bool? isPDFValid;
  bool? isPDFProtected;

  Future<List<String>?> _filePicker(FilePickerParams params) async {
    List<String>? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _pickOrSavePlugin.filePicker(params: params);
    } on PlatformException catch (e) {
      log(e.toString());
    } catch (e) {
      log(e.toString());
    }
    if (!mounted) return result;
    setState(() {
      _isBusy = false;
    });
    return result;
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8.0),
      child: Column(
        children: [
          CustomButton(
              buttonText: 'Pick Single PDF',
              onPressed: _isBusy
                  ? null
                  : () async {
                      final params = FilePickerParams(
                          localOnly: false,
                          mimeTypesFilter: ["application/pdf"],
                          allowedExtensions: [".pdf"]);

                      List<String>? result = await _filePicker(params);

                      if (mounted) {
                        callSnackBar(context: context, text: result.toString());
                      }

                      if (result != null && result.isNotEmpty) {
                        PdfValidityAndProtection? pdfValidityAndProtectionInfo =
                            await _pdfBitmapsPlugin.pdfValidityAndProtection(
                                params: PDFValidityAndProtectionParams(
                                    pdfPath: result[0]));

                        setState(() {
                          isPDFValid = pdfValidityAndProtectionInfo?.isPDFValid;
                          isPDFProtected = pdfValidityAndProtectionInfo
                              ?.isOpenPasswordProtected;
                        });
                      }
                    }),
          const SizedBox(height: 16),
          Text("PDF Validity: $isPDFValid"),
          Text("PDF Protected: $isPDFProtected"),
        ],
      ),
    );
  }
}

class GetPageSizeInfo extends StatefulWidget {
  const GetPageSizeInfo({Key? key}) : super(key: key);

  @override
  State<GetPageSizeInfo> createState() => _GetPageSizeInfoState();
}

class _GetPageSizeInfoState extends State<GetPageSizeInfo> {
  final _pickOrSavePlugin = PickOrSave();
  final _pdfBitmapsPlugin = PdfBitmaps();

  bool _isBusy = false;
  int pageNumber = 1;
  int? widthOfPage;
  int? heightOfPage;

  Future<List<String>?> _filePicker(FilePickerParams params) async {
    List<String>? result;
    try {
      setState(() {
        _isBusy = true;
      });
      result = await _pickOrSavePlugin.filePicker(params: params);
    } on PlatformException catch (e) {
      log(e.toString());
    } catch (e) {
      log(e.toString());
    }
    if (!mounted) return result;
    setState(() {
      _isBusy = false;
    });
    return result;
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8.0),
      child: Column(
        children: [
          CustomButton(
              buttonText: 'Pick Single PDF',
              onPressed: _isBusy
                  ? null
                  : () async {
                      final params = FilePickerParams(
                          localOnly: false,
                          mimeTypesFilter: ["application/pdf"],
                          allowedExtensions: [".pdf"]);

                      List<String>? result = await _filePicker(params);

                      if (mounted) {
                        callSnackBar(context: context, text: result.toString());
                      }

                      if (result != null && result.isNotEmpty) {
                        PageSizeInfo? pageSizeInfo =
                            await _pdfBitmapsPlugin.pdfPageSize(
                                params: PDFPageSizeParams(
                                    pdfPath: result[0],
                                    pageNumber: pageNumber));

                        setState(() {
                          widthOfPage = pageSizeInfo?.widthOfPage;
                          heightOfPage = pageSizeInfo?.heightOfPage;
                        });
                      }
                    }),
          const SizedBox(height: 16),
          Text("Info for Page Number: $pageNumber"),
          Text("Height Of Page: $heightOfPage"),
          Text("Width Of Page: $widthOfPage"),
        ],
      ),
    );
  }
}

class CustomButton extends StatelessWidget {
  const CustomButton({Key? key, required this.buttonText, this.onPressed})
      : super(key: key);

  final String buttonText;
  final void Function()? onPressed;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: OutlinedButton(
              onPressed: onPressed,
              child: Text(buttonText, textAlign: TextAlign.center)),
        ),
      ],
    );
  }
}

callSnackBar({required BuildContext context, required String text}) {
  ScaffoldMessenger.of(context).hideCurrentSnackBar();
  ScaffoldMessenger.of(context).showSnackBar(SnackBar(
    content: Text(text),
  ));
}
