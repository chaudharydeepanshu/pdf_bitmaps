import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'pdf_bitmaps_platform_interface.dart';

/// An implementation of [PdfBitmapsPlatform] that uses method channels.
class MethodChannelPdfBitmaps extends PdfBitmapsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('pdf_bitmaps');

  @override
  Future<int?> pdfPageCount({PDFPageCountParams? params}) async {
    final pageCount = await methodChannel.invokeMethod<int?>(
        'pdfPageCount', params?.toJson());
    return pageCount;
  }

  @override
  Future<Uint8List?> pdfBitmap({PDFBitmapParams? params}) async {
    final Uint8List? bytes = await methodChannel.invokeMethod<Uint8List?>(
        'pdfBitmap', params?.toJson());
    return bytes;
  }

  @override
  Future<List<Uint8List>?> pdfBitmaps({PDFBitmapsParams? params}) async {
    final List? bytesList =
        await methodChannel.invokeMethod<List?>('pdfBitmaps', params?.toJson());
    return bytesList?.cast<Uint8List>();
  }

  @override
  Future<String?> cancelBitmaps() async {
    final String? result =
        await methodChannel.invokeMethod<String?>('cancelBitmaps');
    return result;
  }
}

/// Parameters for the [pdfPageCount] method.
class PDFPageCountParams {
  /// Provide path of pdf file page count.
  final String pdfPath;

  /// Create parameters for the [pdfPageCount] method.
  const PDFPageCountParams({required this.pdfPath});

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
    };
  }
}

class PageInfo {
  /// Provide pdf page number for which you want bitmap.
  final int pageNumber;

  /// Provide pdf page rotation angle.
  final int rotationAngle;

  /// Provide pdf page bitmap scale from 0.1 to 5.
  final double scale;

  /// Provide pdf page background color.
  final Color backgroundColor;

  PageInfo({
    required this.pageNumber,
    this.rotationAngle = 0,
    this.scale = 1,
    this.backgroundColor = Colors.white,
  })  : assert(scale > 0 || scale <= 5,
            'scale should be greater than 0 and less tan or equal to 5'),
        assert(pageNumber > 0, 'pageNumber should be greater than 0');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pageNumber': pageNumber,
      'rotationAngle': rotationAngle,
      'scale': scale,
      'backgroundColor': '#${backgroundColor.value.toRadixString(16)}',
    };
  }

  // Implement toString to make it easier to see information
  // when using the print statement.
  @override
  String toString() {
    return 'PageInfo{pageNumber: $pageNumber, rotationAngle: $rotationAngle, scale: $scale, backgroundColor: $backgroundColor}';
  }
}

/// Parameters for the [pdfBitmap] method.
class PDFBitmapParams {
  /// Provide path of pdf file for bitmap.
  final String pdfPath;

  /// Provide PageInfo for page of pdf for which you want bitmap.
  final PageInfo pageInfo;

  /// Create parameters for the [pdfBitmap] method.
  const PDFBitmapParams({
    required this.pdfPath,
    required this.pageInfo,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pageInfo': pageInfo.toJson(),
    };
  }
}

/// Parameters for the [pdfBitmaps] method.
class PDFBitmapsParams {
  /// Provide path of pdf file for bitmap.
  final String pdfPath;

  /// Provide PageInfo List for pages of pdf for which you want bitmap.
  final List<PageInfo> pagesInfo;

  /// Create parameters for the [pdfBitmaps] method.
  const PDFBitmapsParams({required this.pdfPath, required this.pagesInfo})
      : assert(pagesInfo.length > 0, 'pagesInfo list cant be empty');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pagesInfo': pagesInfo.map((e) => e.toJson()).toList(),
    };
  }
}
